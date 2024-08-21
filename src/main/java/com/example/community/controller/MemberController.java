package com.example.community.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;

import com.example.community.entity.MemberEntity;
import com.example.community.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberRepository memberRepository;

    /**
     * ajax - memberId에 해당하는 memberEntity의 memberGroup 정보 요청 
     * @param memberId
     * @return
     */
    @ResponseBody
    @GetMapping("/member/getMemberGroup")
    public String getMemberGroup(@RequestParam(name = "memberId") String memberId) {
        Optional<MemberEntity> memberEntity = memberRepository.findById(memberId);
        
        if (memberEntity.isPresent()) {
            MemberEntity member = memberEntity.get();
            return member.getMemberGroup();
        }else return "";
    }
    


}
