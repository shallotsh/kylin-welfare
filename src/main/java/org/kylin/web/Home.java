package org.kylin.web;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.kylin.bean.User;
import org.kylin.constant.Constants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Objects;

/**
 * @author shallotsh
 * @date 2017/7/9 下午9:43.
 */
@Controller
@Slf4j
public class Home {

    @RequestMapping("/")
    public String index(){
        return "wyf";
    }

    @RequestMapping("/origin")
    public String origin(){
        return "3d_origin_vs";
    }

    @RequestMapping("/3d-freq")
    public String wyf3DFreq(){
        return "wyf_3d_2sum";
    }

    @RequestMapping("/2sum-dict")
    public String wyf3DDict(){
        return "wyf_2sum_dict";
    }


    @RequestMapping("/2d")
    public String wyf2d(){
        return "wyf_2d";
    }

    @RequestMapping("/frame")
    public String frame(){
        return "wyf_frame";
    }

    @RequestMapping("/expert")
    public String expert(){
        return "wyf_expert";
    }

    @RequestMapping("/4d")
    public String wyf4d(){
        return "wyf_4d";
    }

    @RequestMapping("/tail-sum")
    public String wyfTailSum(){
        return "wyf_tail_sum";
    }


    @RequestMapping("/expert_inner")
    public String expertInner(){
        return "wyf_expert_inner";
    }


    @RequestMapping("/login")
    public String login(String origin, HttpServletRequest request){

        HttpSession session = request.getSession();
        session.removeAttribute(Constants.LOGIN_STATUS_KEY);
        String prePage;
        if(StringUtils.isNotEmpty(origin) && origin.startsWith("/")){
            prePage = origin;
        }else {
            prePage = request.getHeader("Referer");
        }
        session.setAttribute("prePage", prePage);
        log.info("prePage={}", prePage);

        return "login";
    }

    @RequestMapping(value = "/login/auth", method = RequestMethod.POST)
    public String loginCheck(User user, HttpServletRequest request){

        if(Objects.isNull(user)){
            return "redirect:/login";
        }

        log.info("login user:{}", user);
        HttpSession session = request.getSession();

        if("wyf".equals(user.getUserName())){

            // 登录成功
            session.setAttribute(Constants.LOGIN_STATUS_KEY, Constants.LOGIN_SUCCESS);
            session.setMaxInactiveInterval(7 * 24 * 60 * 60);

            Object prePage = session.getAttribute("prePage");
            if(Objects.isNull(prePage) || prePage.toString().endsWith("login")) {
                return "redirect:/";
            }else{
                return "redirect:" + prePage.toString();
            }
        }

        session.removeAttribute(Constants.LOGIN_STATUS_KEY);


        return "redirect:/login";
    }

}
