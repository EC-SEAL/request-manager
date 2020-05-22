/**
 * NOTE: This class is auto generated by the swagger code generator program (2.4.9).
 * https://github.com/swagger-api/swagger-codegen
 * Do not edit the class manually.
 */
package eu.atos.seal.rm.controller;

import io.swagger.annotations.*;
import org.springframework.ui.Model;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2019-11-07T15:11:31.760Z")

@Api(value = "response", description = "the response API")
public interface ResponseApi 
{

    @ApiOperation(value = "Callback. Pass a standard response object to be handled.", nickname = "responsePost", notes = "Process an Authn or Data response", tags={ "RequestManager", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Response admitted"),
        @ApiResponse(code = 400, message = "Bad response"),
        @ApiResponse(code = 401, message = "Not authorised") })
    @RequestMapping(value = "/rm/response",
        consumes = { "application/x-www-form-urlencoded" },
        method = RequestMethod.POST)
    //ResponseEntity<Void> responsePost(@ApiParam(value = "The security token for ms to ms calls", required=true) @RequestParam(value="msToken", required=true)  String msToken,Model model);
    String responsePost(@ApiParam(value = "The security token for ms to ms calls", required=true) @RequestParam(value="msToken", required=true)  String msToken,Model model);

}
