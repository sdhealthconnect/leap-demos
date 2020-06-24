# Consent Enforcement Service (CES) V2 Orchestration

This module provides an API for Inbound V2 Message Exchange. 
The API will receive a V2 message and will process the Message and check the Authorization Decision.
When the **desicion** is a `Concent permit` the **action** message will be checked having into account also 
the **sdlDecision** to finally determine the restrictions existing on Message Segments. 

A table can summarize how the classification is performed:

| Action | sdlDecision | Message classification |
| :---: | :---: | :---: |
| REDACT | RESTRICTED | Segments of this V2 message have been redacted based on Patient Privacy concerns - PPS |
| REDACT | NOT RESTRICTED | No sensitive segments found allow release of message for further processing |
| (*1) | RESTRICTED | No patient constraints.  Organization policy does not allow the receipt of sensitive information.  Segnments of this message have been redacted. |
| (*1) | NOT RESTRICTED | No Patient constraints and SLS have determined message segments are not restricted - Allowing forward. |
| (*1) | (*1) | Unable to determine any constraints on this document.  Allowing forward. |

:pencil: _(*1) Not Specified, empty or null_

When the **desicion** is not `Concent permit` the authorization for this V2 messaging action is not permitted.

## The `HL7v2` Demo
- Launch the [Swagger UI](http://localhost:9092/swagger-ui.html) and find the method **V2-Message-Controller** or follow
this [link](http://localhost:9092/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#/V2-Message-Controller/processMessage) 
- Click on the "Try It Out" button.
- Select "text/plain" in Request Body type drop-down list
- Copy the contents of `./test-script/message-artifacts/v2alcoholabuse.txt` in the Request Body.
- Click on Execute button.

The following shows a sample output:
![Swagger-UI](../docs/assets/swaggerinterface.png?raw=true)

In your Docker-Compose terminal session you should see the following response
![Test Response](../docs/assets/testoutput.png?raw=true)

Note:  If you wish to run this demo multiple times with this message, keep in mind that the message ID should be changed to a unique value every time. This is the _third_ last field in the HL7v2 message string. For example, change the following message 
```
MSH|^~\&|SendingApp^‹OID›^ISO|SendingFac^‹OID›^ISO|ReceivingApp^‹OID›^ISO|ReceivingFac^2.16.840.1.113883.20.5^ISO|2007509101832133||ADT^A08^ADT_A01|20075091019450028|D|2.5
```
to
```
MSH|^~\&|SendingApp^‹OID›^ISO|SendingFac^‹OID›^ISO|ReceivingApp^‹OID›^ISO|ReceivingFac^2.16.840.1.113883.20.5^ISO|2007509101832133||ADT^A08^ADT_A01|20075091019450029|D|2.5
```
