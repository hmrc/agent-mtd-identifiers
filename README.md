
# agent-mtd-identifiers
 
[![Build Status](https://travis-ci.org/hmrc/agent-mtd-identifiers.svg?branch=master)](https://travis-ci.org/hmrc/agent-mtd-identifiers) [ ![Download](https://api.bintray.com/packages/hmrc/releases/agent-mtd-identifiers/images/download.svg) ](https://bintray.com/hmrc/releases/agent-mtd-identifiers/_latestVersion)

Micro-library for typing and validating UK tax identifiers within the Agents domain.

#### Identifier Types

Types are provided for many common tax identifiers, such as:

* [Agent Reference Number (Arn)](src/main/scala/uk/gov/hmrc/agentmtdidentifiers/model/Arn.scala)
* [Invitation Id](src/main/scala/uk/gov/hmrc/agentmtdidentifiers/model/InvitationId.scala)
* [Unique Taxpayer References (UTR)](src/main/scala/uk/gov/hmrc/agentmtdidentifiers/model/Utr.scala)
* [MtdItId](src/main/scala/uk/gov/hmrc/agentmtdidentifiers/model/MtdItId.scala)
* [VAT Registration Number (VRN)](src/main/scala/uk/gov/hmrc/agentmtdidentifiers/model/Vrn.scala)

#### JSON handling

`Reads` and `Writes` have been provided for Play's JSON library for all identifiers.

#### Validation
`isValid` method is available for all the identifiers.

```scala
Arn.isValid("TARN0000001") // true
Arn.isValid("TABC0000001") // false
```

```
### Installing

Add the following to your SBT build:
```scala
resolvers += Resolver.bintrayRepo("hmrc", "releases")

libraryDependencies += "uk.gov.hmrc" % "agent-mtd-identifiers" % "[INSERT VERSION]"
```


### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
    
