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

import com.google.inject.{Inject, Singleton}
import io.swagger.v3.parser.OpenAPIV3Parser
import io.swagger.v3.parser.core.models.ParseOptions
import play.api.libs.Files
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents, MultipartFormData}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.simpleapideploymentstubs.models.{CreateMetadata, DeploymentResponse, DeploymentsResponse, FailuresResponse, UpdateMetadata}

@Singleton
class SimpleAPiDeploymentController @Inject()(cc: ControllerComponents) extends BackendController(cc) {

  def validate(): Action[String] = Action(parse.tolerantText) {
    request =>
      if (validOas(request.body)) {
        Ok
      }
      else {
        BadRequest(Json.toJson(FailuresResponse.cannedResponse))
      }
  }

  def create(): Action[MultipartFormData[Files.TemporaryFile]] = Action(parse.multipartFormData) {
    request =>
      (request.body.dataParts.get("metadata"), request.body.dataParts.get("openapi")) match {
        case (Some(Seq(metadata)), Some(Seq(openapi))) =>
          Json.parse(metadata).validate[CreateMetadata].fold(
            _ => BadRequest,
            validMetadata =>
              if (validOas(openapi)) {
                Ok(Json.toJson(DeploymentsResponse(validMetadata.name)))
              }
              else {
                BadRequest(Json.toJson(FailuresResponse.cannedResponse))
              }
          )
        case _ => BadRequest
      }
  }

  def update(serviceId: String): Action[MultipartFormData[Files.TemporaryFile]] = Action(parse.multipartFormData) {
    request =>
      (request.body.dataParts.get("metadata"), request.body.dataParts.get("openapi")) match {
        case (Some(Seq(metadata)), Some(Seq(openapi))) =>
          Json.parse(metadata).validate[UpdateMetadata].fold(
            _ => BadRequest,
            _ =>
              if (validOas(openapi)) {
                Ok(Json.toJson(DeploymentsResponse(serviceId)))
              }
              else {
                BadRequest(Json.toJson(FailuresResponse.cannedResponse))
              }
          )
        case _ => BadRequest
      }
  }

  private def validOas(oas: String): Boolean = {
    val options: ParseOptions = new ParseOptions()
    options.setResolve(false)

    Option(new OpenAPIV3Parser().readContents(oas, null, options).getOpenAPI).isDefined
  }

  def deployment(publisherReference: String): Action[AnyContent] = Action {
    Ok(Json.toJson(DeploymentResponse.apply(publisherReference)))
  }

}
