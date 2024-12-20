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

case class DetailsResponse(
  description: String,
  status: String,
  domain: String,
  subdomain: String,
  backends: Seq[String],
  egressMappings: Option[Seq[EgressMapping]],
  prefixesToRemove: Seq[String]
)

object DetailsResponse {

  val cannedResponse: DetailsResponse = DetailsResponse(
    description = "A short description of the API",
    status = "ALPHA",
    domain = "8",
    subdomain = "8.1",
    backends = Seq("NPS"),
    egressMappings = Some(Seq(EgressMapping("/prefix", "/egress-prefix"))),
    prefixesToRemove = Seq("/v1")
  )

  implicit val formatDetailsResponse: Format[DetailsResponse] = Json.format[DetailsResponse]

}
