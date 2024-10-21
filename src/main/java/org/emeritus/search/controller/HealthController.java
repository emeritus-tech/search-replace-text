package org.emeritus.search.controller;

import javax.servlet.http.HttpServletRequest;
import org.emeritus.search.constant.URLConstants;
import org.emeritus.search.dto.GlobalApiResponse;
import org.emeritus.search.dto.PingDto;
import org.emeritus.search.service.CanvasService;
import org.emeritus.search.utils.RestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Api(tags = "Health check controller")
@Tag(name = "Health check controller", description = "This controller checks the app health")
@RequestMapping("/api/v1/")
public class HealthController {

  /** The logger. */
  private Logger logger = LoggerFactory.getLogger(HealthController.class);

  /** The canvas service. */
  @Autowired
  private CanvasService canvasService;

  @GetMapping("/health-check")
  public String healthCheck() {
    return "Success!";
  }

  /**
   * Execute ping.
   *
   * @param request the request
   * @return the response entity
   */
  @ApiOperation(value = "Ping canvas search", notes = "Returns status of canvas search")
  @GetMapping(URLConstants.PING)
  public ResponseEntity<GlobalApiResponse<PingDto>> executePing(HttpServletRequest request) {
    String userId = null;
    try {
      userId = canvasService.getCanvasUserId();
    } catch (Exception e) {
      logger.info("Error while getting CanvasUserId", e);
    }
    return RestUtils.successResponse(
        PingDto.builder().context(request.getContextPath()).userId(userId).build(), HttpStatus.OK);
  }
}
