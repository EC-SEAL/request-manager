/**
Copyright © 2020  Atos Spain SA. All rights reserved.
This file is part of SEAL Request Manager (SEAL rm).
SEAL rm is free software: you can redistribute it and/or modify it under the terms of EUPL 1.2.
THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT ANY WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT, 
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
DAMAGES OR OTHER LIABILITY, WHETHER IN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
See README file for the full disclaimer information and LICENSE file for full license information in the project root.

@author Atos Research and Innovation, Atos SPAIN SA
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
    ResponseEntity<Void> responsePost(@ApiParam(value = "The security token for ms to ms calls", required=true) @RequestParam(value="msToken", required=true)  String msToken,Model model);
    //String responsePost(@ApiParam(value = "The security token for ms to ms calls", required=true) @RequestParam(value="msToken", required=true)  String msToken, Model model);

}
