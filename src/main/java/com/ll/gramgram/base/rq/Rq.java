package com.ll.gramgram.base.rq;

import com.ll.gramgram.boundedContext.member.entity.Member;
import com.ll.gramgram.boundedContext.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
@Component
@RequestScope
public class Rq {
    private final HttpServletRequest req;
    private final HttpServletResponse res;
    private final HttpSession session;
    private final MemberService memberService;
    private final User user;
    private Member member = null; // 레이지 로딩: 처음부터 넣지 않고, 요청될 때 넣는다

    public Rq(HttpServletRequest req, HttpServletResponse res, HttpSession session, MemberService memberService) {
        this.req = req;
        this.res = res;
        this.session = session;
        this.memberService = memberService;

        // 현재 로그인한 회원의 인증정보를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof User) {
            this.user = (User) authentication.getPrincipal();
        } else {
            this.user = null;
        }
    }

    public boolean isLogin() {
        return user != null;
    }

    public boolean isLogout() {
        return !isLogin();
    }

    public Member getMember() {
        if (isLogout()) return null;

        // 데이터가 없는지 체크
        if (member == null) {
            member = memberService.findByUsername(user.getUsername()).orElseThrow();
        }

        return member;
    }

    public String historyBack(String msg) {
        // model.addAttribute와 같은 의미
        req.setAttribute("alertMsg", msg);
        return "common/js";
    }
}
