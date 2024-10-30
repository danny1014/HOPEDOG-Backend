package com.example.hope_dog.controller.adopt;

import com.example.hope_dog.dto.adopt.adopt.*;
import com.example.hope_dog.dto.page.Criteria;
import com.example.hope_dog.dto.page.Page;
import com.example.hope_dog.service.adopt.adopt.AdoptService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/adopt")    //시작도메인 localhost:8060/adopt
public class AdoptController {
    private final AdoptService adoptService;

    //입양/임보/후기 메인페이지
    @GetMapping("/main")        //열릴페도메인 localhost:8060/adopt/main
    public String Main(Model model) {
        List<MainDTO> MainList = adoptService.getMainList();

        model.addAttribute("MainList", MainList);
        return "adopt/adopt-main";  //localhost:8060/adopt/main로 접속했을시 열릴 html
    }

    //    입양메인
    @GetMapping("/adopt")
    public String adoptList(Criteria criteria, Model model, HttpSession session){
        List<AdoptMainDTO> adoptMainList = adoptService.findAllPage(criteria);
        int total = adoptService.findTotal();
        Page page = new Page(criteria, total);
        Long centerMemberNo = (Long) session.getAttribute("centerMemberNo"); //이것도 무시 세션값 자겨와서 저장

        model.addAttribute("AdoptMainList", adoptMainList);
        model.addAttribute("page", page);
        model.addAttribute("centerMemberNo", centerMemberNo); //이건 나만 쓰는거 무시 세션값 html에서 쓸수있게 model추가

        return "adopt/adopt/adopt-adopt";
    }

    //입양상세글
    @GetMapping("/adopt/adoptdetail")
    public String adoptDetail(@RequestParam("adoptNo") Long adoptNo, Model model, HttpSession session) {
        List<AdoptDetailDTO> adoptDetailList = adoptService.getAdoptDetail(adoptNo);
        List<AdoptCommentDTO> adoptCommentlList = adoptService.getAdoptComment(adoptNo);
        Long centerMemberNo = (Long) session.getAttribute("centerMemberNo");
        Long memberNo = (Long) session.getAttribute("memberNo");

        model.addAttribute("adoptDetailList", adoptDetailList);
        model.addAttribute("adoptCommentList", adoptCommentlList);
        model.addAttribute("centerMemberNo", centerMemberNo);
        model.addAttribute("memberNo", memberNo);

        return "adopt/adopt/adopt-adoptdetail";
    }

    //입양글작성페이지이동
    @GetMapping("/adopt/adoptwrite")
    public String adoptWrite(HttpSession session, Model model) {
        // 세션에서 memberNo 가져오기
        Long centerMemberNo = (Long) session.getAttribute("centerMemberNo");

        // 모델에 memberNo 추가
        model.addAttribute("centerMemberNo", centerMemberNo);

        return "adopt/adopt/adopt-adoptwrite"; // 템플릿 이름
    }

    // 입양 글 등록 처리
    @PostMapping("/adopt/adoptWriteRegi")
    public String postAdoptWrite(
            @DateTimeFormat(pattern = "yyyy-MM-dd") AdoptWriteDTO adoptWriteDTO) {
        // 서비스 호출하여 데이터베이스에 저장
        adoptService.registerAdoption(adoptWriteDTO);
        return "redirect:/adopt/adopt";
    }


    //입양글수정
    @GetMapping("/adopt/adoptmodify")
    public String adoptModify() {
        return "adopt/adopt/adopt-adoptmodify";
    }

    // 입양 신청서 페이지 열기
    @GetMapping("/adopt/adoptrequest")
    public String adoptRequest(HttpSession session, Model model) {
        Long memberNo = (Long) session.getAttribute("memberNo");
        model.addAttribute("memberNo", memberNo);

        return "adopt/adopt/adopt-adoptrequest";
    }

    // 입양 신청서 등록
    @PostMapping("/adopt/adoptrequestRegi")
    public String adoptRequestRegi(@DateTimeFormat(pattern = "yyyy-MM-dd") AdoptRequestDTO adoptRequestDTO) {
        return "redirect:/adopt/adopt";
    }

    //임시보호 메인
    @GetMapping("/protect")
    public String protectMain() {

        return "adopt/protect/adopt-protect";
    }

    //임시보호상세글
    @GetMapping("/protect/protectdetail")
    public String protectDetail() {
        return "adopt/protect/adopt-protectdetail";
    }

    //임시보호글작성
    @GetMapping("/protect/protectwrite")
    public String protectWrite() {
        return "adopt/protect/adopt-protectwrite";
    }

    //임시보호글수정
    @GetMapping("/protect/protectmodify")
    public String protectModify() {
        return "adopt/protect/adopt-protectmodify";
    }

    //후기메인
    @GetMapping("/review")
    public String reviewMain() {
        return "adopt/review/adopt-review";
    }

    //후기상세글
    @GetMapping("/review/reviewdetail")
    public String reviewDetail() {
        return "adopt/review/adopt-reviewdetail";
    }

    //후기글작성
    @GetMapping("/review/reviewwrite")
    public String reviewWrite() {
        return "adopt/review/adopt-reviewwrite";
    }

    //후기글수정
    @GetMapping("/review/reviewmodify")
    public String reviewModify() {
        return "adopt/review/adopt-reviewmodify";
    }


}
