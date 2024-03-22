/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.simpleapideploymentstubs.controllers

import org.scalatest.OptionValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.mvc.MultipartFormData
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.simpleapideploymentstubs.models.{DeploymentsResponse, Metadata, ValidationFailure}

class SimpleAPiDeploymentControllerSpec extends AnyFreeSpec with Matchers with OptionValues {

  import SimpleAPiDeploymentControllerSpec._

  "validate" - {
    "must return 200 Ok when the OAS document is valid" in {
      val application = buildApplication()

      running(application) {
        val request = FakeRequest(routes.SimpleAPiDeploymentController.validate())
          .withHeaders("Content-Type" -> "application/yaml")
          .withBody(oas)
        val result = route(application, request).value

        status(result) mustBe OK
      }
    }

    "must return 400 BadRequest and a ValidationFailuresResponse when the OAS document is invalid" in {
      val application = buildApplication()

      running(application) {
        val request = FakeRequest(routes.SimpleAPiDeploymentController.validate())
          .withHeaders("Content-Type" -> "application/yaml")
          .withBody("rhubarb")
        val result = route(application, request).value

        status(result) mustBe BAD_REQUEST
        contentAsJson(result) mustBe Json.toJson(ValidationFailure.cannedResponse)
      }
    }
  }

  "deployments" - {
    "must return 200 Ok and a DeploymentsResponse when the request is valid" in {
      val application = buildApplication()

      val metadata = Metadata(
        lineOfBusiness = "test-lob",
        name = "test-name",
        description = "test-description",
        egress = "test-egress"
      )

      running(application) {
        val request = FakeRequest(routes.SimpleAPiDeploymentController.deployments())
          .withMultipartFormDataBody(
            MultipartFormData(
              dataParts = Map(
                "metadata" -> Seq(Json.toJson(metadata).toString()),
                "openapi" -> Seq(oas)
              ),
              files = Seq.empty,
              badParts = Seq.empty
            )
          )

        val result = route(application, request).value

        status(result) mustBe OK
        contentAsJson(result) mustBe Json.toJson(DeploymentsResponse(metadata))
      }
    }

    "must return 400 BadRequest and a ValidationFailuresResponse when the OAS document is invalid" in {
      val application = buildApplication()

      val metadata = Metadata(
        lineOfBusiness = "test-lob",
        name = "test-name",
        description = "test-description",
        egress = "test-egress"
      )

      running(application) {
        val request = FakeRequest(routes.SimpleAPiDeploymentController.deployments())
          .withMultipartFormDataBody(
            MultipartFormData(
              dataParts = Map(
                "metadata" -> Seq(Json.toJson(metadata).toString()),
                "openapi" -> Seq("rhubarb")
              ),
              files = Seq.empty,
              badParts = Seq.empty
            )
          )

        val result = route(application, request).value

        status(result) mustBe BAD_REQUEST
        contentAsJson(result) mustBe Json.toJson(ValidationFailure.cannedResponse)
      }
    }

    "must return 400 Bad Request when the metadata is invalid" in {
      val application = buildApplication()

      running(application) {
        val request = FakeRequest(routes.SimpleAPiDeploymentController.deployments())
          .withMultipartFormDataBody(
            MultipartFormData(
              dataParts = Map(
                "metadata" -> Seq(Json.obj().toString()),
                "openapi" -> Seq(oas)
              ),
              files = Seq.empty,
              badParts = Seq.empty
            )
          )

        val result = route(application, request).value

        status(result) mustBe BAD_REQUEST
      }
    }
  }

  private def buildApplication(): Application = {
    GuiceApplicationBuilder()
      .build()
  }

}

object SimpleAPiDeploymentControllerSpec {

  val oas: String =
    """
      |openapi: 3.0.0
      |info:
      |  version: 1.0.0
      |  title: Single Path
      |  description: This is a slimmed down single path version of the Petstore definition.
      |servers:
      |  - url: https://httpbin.org
      |paths:
      |  '/pet/{id}':
      |    parameters:
      |      - name: id
      |        in: path
      |        required: true
      |        schema:
      |          type: integer
      |    put:
      |      tags:
      |        - pet
      |      summary: Update a pet
      |      description: This operation will update a pet in the database.
      |      responses:
      |        '400':
      |          description: Invalid id value
      |      security:
      |        - apiKey: []
      |    get:
      |      tags:
      |        - pet
      |      summary: Find a pet
      |      description: This operation will find a pet in the database.
      |      responses:
      |        '400':
      |          description: Invalid status value
      |      security: []
      |components:
      |  securitySchemes:
      |    apiKey:
      |      type: http
      |      scheme: basic
      |""".stripMargin

}
