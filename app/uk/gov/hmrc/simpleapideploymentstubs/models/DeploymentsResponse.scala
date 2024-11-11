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

package uk.gov.hmrc.simpleapideploymentstubs.models

import play.api.libs.json.{Format, Json}

case class DeploymentsResponse(id: String, version: String, mergeRequestIid: Int, uri: String)

object DeploymentsResponse {

  def apply(serviceId: String): DeploymentsResponse = DeploymentsResponse(
    id = serviceId,
    version = "0.1.0",
    mergeRequestIid = 123,
    uri = s"/internal/v1/simple-api-deployment/deployments/$serviceId/status?mr-iid=123&version=0.1.0"
  )

  implicit val formatDeploymentsResponse: Format[DeploymentsResponse] = Json.format[DeploymentsResponse]

}

case class InvalidOasResponse(failure: FailuresResponse)

object InvalidOasResponse {

  implicit val formatInvalidOasResponse: Format[InvalidOasResponse] = Json.format[InvalidOasResponse]

}
