package com.lhq.superboot.controller.wx.official;

import com.github.pagehelper.util.StringUtil;
import com.lhq.superboot.service.OfficialCoreService;
import com.lhq.superboot.wechat.util.WeChatOfficialUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Description:
 * @author: lihaoqi
 * @date: 2019年9月5日 下午5:05:35
 * @version: v1.0.0
 */
@CrossOrigin(origins = "*", allowCredentials = "true", allowedHeaders = "*")
@RestController
@RequestMapping("/wx/official")
@Api(value = "微信公众号controller", tags = {"微信公众号接口"})
public class WxOfficialAccountController {

    private static final Logger logger = LoggerFactory.getLogger(WxOfficialAccountController.class);

    @Autowired
    private OfficialCoreService coreService;

    /**
     * @return
     * @throws IOException
     * @Description: get方法，验证消息的确来自微信服务器，点击“提交”按钮时，请求的就是该方法
     */
    @ApiOperation(value = "微信验证消息", notes = "微信验证消息")
    @GetMapping("/connect")
    public Object connectWxGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 消息来源可靠性验证
        String signature = req.getParameter("signature");// 微信加密签名
        String timestamp = req.getParameter("timestamp");// 时间戳
        String nonce = req.getParameter("nonce"); // 随机数
        String echostr = req.getParameter("echostr");// 成为开发者验证
        // 确认此次GET请求来自微信服务器，原样返回echostr参数内容，则接入生效，成为开发者成功，否则接入失败
        PrintWriter out = resp.getWriter();
        if (WeChatOfficialUtils.checkSignature(signature, timestamp, nonce)) {
            logger.debug("[connectWxGet] -> 微信连接校验成功{}", echostr);
            out.print(echostr);
        }
        out.close();
        return null;
    }

    /**
     * @return
     * @throws IOException
     * @Description: post方法，现业务逻辑都走该方法，当然前提是上面的“提交”能成功。
     */
    @ApiOperation(value = "微信业务逻辑", notes = "微信业务逻辑")
    @PostMapping("/connect")
    public Object connectWxPost(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        // 响应信息与接收信息时，使用UTF-8编码防止乱码
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        // 处理微信信息并返回
        String message = coreService.officialMessageHandle(req);
        if (StringUtil.isEmpty(message)) {
            logger.error("[connectWxPost] -> 微信处理信息失败");
            return null;
        } else {
            logger.debug("[connectWxPost] -> 微信处理信息完成，返回信息:\n{}", message);
            return message;
        }

    }
}
