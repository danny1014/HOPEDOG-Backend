package com.example.hope_dog.controller.mypage;


import com.example.hope_dog.dto.mypage.*;
import com.example.hope_dog.service.mypage.MpNoteBoxService;
import com.example.hope_dog.service.mypage.MypageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession; // Jakarta EE 사용
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/mypage")
public class MypageController {
    //로그인 세션
    private final HttpSession session;


    private MypageService mypageService;

    @Autowired
    public MypageController(HttpSession session, MypageService mypageService, MpNoteBoxService mpNoteBoxService) {
        this.session = session;
        this.mypageService = mypageService;
        this.mpNoteBoxService = mpNoteBoxService;
    }

    @GetMapping("/mypage")
    public String profile(HttpSession session, Model model) {
        Long memberNo = (Long) session.getAttribute("memberNo"); // 세션에서 memberNo 가져오기

        if (memberNo == null) {
            // memberNo가 없을 경우 적절한 처리 (예: 로그인 페이지로 리다이렉트)
            return "redirect:/login"; // 예시: 로그인 페이지로 리다이렉트
        }

        List<MypageDTO> mypageProfile = mypageService.getMypageProfile(memberNo);
        model.addAttribute("mypageProfile", mypageProfile);

        return "mypage/mypage-profile";
    }

    // 개인정보수정
    @GetMapping("/update")
    public String updateProfile(Model model) {
        Long memberNo = (Long) session.getAttribute("memberNo");

        // 프로필 정보 가져오기
        MypageViewProfileDTO profile = mypageService.getMypageViewProfile(memberNo);
        model.addAttribute("profile", profile);

        return "mypage/mypage-update";
    }

    // 닉네임 중복 체크
    @GetMapping("/checkNickname")
    @ResponseBody  // JSON 형식으로 응답 본문을 반환하도록 설정
    public ResponseEntity<Map<String, Boolean>> checkedNickname(
            // newNickname: 사용자가 입력한 새 닉네임, currentNickname: 현재 사용자가 가지고 있는 닉네임 (수정할 경우)
            @RequestParam(name = "newNickname") String newNickname,
            @RequestParam(name = "currentNickname") String currentNickname) {

        // newNickname이 현재 닉네임과 다를 경우, 중복 여부를 확인
        boolean available = mypageService.checkedNickname(newNickname, currentNickname);

        // 중복 여부(available)를 JSON 형태로 응답
        return ResponseEntity.ok(Map.of("available", available));
        // 'available' 값이 true이면 사용 가능, false이면 이미 사용 중인 닉네임
    }


    // 이메일 중복 체크
    @GetMapping("/checkEmail")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> checkedEmail(
            @RequestParam(name = "newEmail") String newEmail,
            @RequestParam(name = "currentEmail") String currentEmail) {

        // 중복 검사 수행
        boolean available = mypageService.updateCheckedEmail(newEmail, currentEmail);
        return ResponseEntity.ok(Map.of("available", available));
    }

    @PostMapping("/updateProfileOk")
    public String updateProfileOk(@ModelAttribute MypageUpdateProfileDTO mypageUpdateProfileDTO, RedirectAttributes redirectAttributes) {
        Long memberNo = (Long) session.getAttribute("memberNo");
        if (memberNo == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/main/main";
        }

        // memberNo를 DTO에 설정
        mypageUpdateProfileDTO.setMemberNo(memberNo);

        // 프로필 업데이트 시도
        try {
            int updateCount = mypageService.updateProfile(mypageUpdateProfileDTO);
            if (updateCount > 0) {
                redirectAttributes.addFlashAttribute("successMessage", "프로필이 성공적으로 업데이트되었습니다.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "프로필 업데이트에 실패했습니다.");
            }
        } catch (Exception e) {
            log.error("프로필 업데이트 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("errorMessage", "프로필 업데이트 중 오류가 발생했습니다.");
        }

        return "redirect:/mypage/update"; // 업데이트 후 리다이렉트할 페이지
    }

    @GetMapping("/adopt")
    public String adopt(HttpSession session, Model model) {
        Long memberNo = (Long) session.getAttribute("memberNo"); // 세션에서 memberNo 가져오기

        if (memberNo == null) {
            // memberNo가 없을 경우 적절한 처리 (예: 로그인 페이지로 리다이렉트)
            return "redirect:/login"; // 예시: 로그인 페이지로 리다이렉트
        }

        List<MypageAdoptListDTO> mypageAdoptList = mypageService.getMypageAdoptProfile(memberNo);
        model.addAttribute("mypageAdoptList", mypageAdoptList);

        return "mypage/mypage-adopt";
    }

    @GetMapping("/protect")
    public String protect(HttpSession session, Model model) {
        Long memberNo = (Long) session.getAttribute("memberNo");

        if (memberNo == null) {
            return "redirect:/login";
        }

        List<MypageProtectDTO> mypageProtectList = mypageService.getMypageProtectProfile(memberNo);
        model.addAttribute("mypageProtectList", mypageProtectList);

        return "mypage/mypage-protect";
    }

    @GetMapping("/volun")
    public String volunList(HttpSession session, Model model) {
        Long memberNo = (Long) session.getAttribute("memberNo");
        if (memberNo == null) {
            return "redirect:/login";
        }

        List<MypageVolunDTO> mypageVolunList = mypageService.getMypageVolunProfile(memberNo);
        model.addAttribute("mypageVolunList", mypageVolunList);

        return "mypage/mypage-volun-list";
    }

    @GetMapping("/posts")
    public String posts(HttpSession session, Model model) {
        Long memberNo = (Long) session.getAttribute("memberNo");
        if (memberNo == null) {
            return "redirect:/login";
        }

        List<MypagePostsDTO> mypagePostsList = mypageService.getMypagePostsProfile(memberNo);
        model.addAttribute("mypagePostsList", mypagePostsList);

        return "mypage/mypage-posts";
    }


    //쪽지함
    private final MpNoteBoxService mpNoteBoxService;

    //보낸쪽지함
    @GetMapping("/noteboxSendList")
    public String sendList(Model model) {
        Long memberNo = (Long) session.getAttribute("memberNo"); // 세션에서 회원 번호 가져오기
        if (memberNo == null) {
            log.error("세션에서 centerMemberNo를 찾을 수 없습니다.");
            return "redirect:/login"; // 세션이 없으면 로그인 페이지로 리다이렉트
        }

        List<MypageNoteSendDTO> noteboxSendList = mpNoteBoxService.getSendList(memberNo);
        model.addAttribute("noteboxSendList", noteboxSendList);
        log.info("SendList 요청이 들어왔습니다. memberNo: {}", memberNo);

        return "mypage/mypage-outbox"; // HTML 파일 경로
    }

    //받은쪽지함
    @GetMapping("/noteboxReceiveList")
    public String receiveList(Model model) {
        Long memberNo = (Long) session.getAttribute("memberNo"); // 세션에서 회원 번호 가져오기
        if (memberNo == null) {
            log.error("세션에서 memberNo를 찾을 수 없습니다.");
            return "redirect:/login"; // 세션이 없으면 로그인 페이지로 리다이렉트
        }


        List<MypageNoteReceiveDTO> noteboxReceiveList = mpNoteBoxService.getReceiveList(memberNo);
        model.addAttribute("noteboxReceiveList", noteboxReceiveList);
        log.info("ReceiveList 요청이 들어왔습니다. memberNo: {}", memberNo);

        return "mypage/mypage-inbox"; // HTML 파일 경로
    }

    // 보낸 쪽지 상세 페이지
    @GetMapping("/noteboxSendDetail")
    public String getNoteboxSendDetail(@RequestParam("noteboxSendNo") Long noteboxSendNo, Model model) {
        MpNoteboxSendDetailDTO mpNoteboxSendDetail = mpNoteBoxService.getNoteboxSendDetail(noteboxSendNo);

        // 쪽지 정보를 찾지 못한 경우
        if (mpNoteboxSendDetail == null) {
            log.error("보낸 쪽지를 찾을 수 없습니다: {}", noteboxSendNo);
            return "redirect:/mypage/noteboxSendList"; // 쪽지를 찾지 못하면 목록으로 리다이렉트
        }

        model.addAttribute("mpNoteboxSendDetail", mpNoteboxSendDetail); // 모델에 쪽지 상세 정보 추가
        return "mypage/mypage-outbox-detail"; // 상세 페이지의 템플릿 이름
    }

    // 받은 쪽지 상세 페이지
    @GetMapping("/noteboxReceiveDetail")
    public String getNoteboxReceiveDetail(@RequestParam("noteboxReceiveNo") Long noteboxReceiveNo, Model model) {
        MpNoteboxReceiveDetailDTO mpNoteboxReceiveDetail = mpNoteBoxService.getNoteboxReceiveDetail(noteboxReceiveNo);

        // 쪽지 정보를 찾지 못한 경우
        if (mpNoteboxReceiveDetail == null) {
            log.error("보낸 쪽지를 찾을 수 없습니다: {}", noteboxReceiveNo);
            return "redirect:/mypage/noteboxReceiveList"; // 쪽지를 찾지 못하면 목록으로 리다이렉트
        }

        model.addAttribute("mpNoteboxReceiveDetail", mpNoteboxReceiveDetail); // 모델에 쪽지 상세 정보 추가
        return "mypage/mypage-inbox-detail"; // 상세 페이지의 템플릿 이름
    }


    //쪽지보내기 페이지 이동
    @GetMapping(value = "/noteboxWrite")
    public String noteboxWrite(Model model, HttpSession session) {
        Long memberNo = (Long) session.getAttribute("memberNo"); // 세션에서 센터회원 번호 가져오기
        if (memberNo == null) {
            log.error("세션에서 memberNo를 찾을 수 없습니다.");
            return "redirect:/login"; // 세션이 없으면 로그인 페이지로 리다이렉트
        }

        // DTO 객체 생성 및 모델에 추가
        model.addAttribute("mpNoteboxWriteDTO", new MpNoteboxWriteDTO());

        return "mypage/mypage-notebox-write"; // HTML 파일 경로
    }

    //쪽지보내기 (답장) 페이지 이동
    @GetMapping("/noteboxResponse")
    public String writeNote(@RequestParam("noteboxSenderName") String noteboxSenderName, Model model, HttpSession session) {
        // 세션 체크 및 DTO 설정
        Long memberNo = (Long) session.getAttribute("memberNo");
        if (memberNo == null) {
            log.error("세션에서 memberNo를 찾을 수 없습니다.");
            return "redirect:/login";
        }

        MpNoteboxWriteDTO mpNoteboxWriteDTO = new MpNoteboxWriteDTO();
        mpNoteboxWriteDTO.setNoteboxReceiverName(noteboxSenderName); // senderName을 받는 사람 이름으로 설정
        model.addAttribute("mpNoteboxWriteDTO", mpNoteboxWriteDTO);

        return "mypage/mypage-notebox-write";
    }

    // 쪽지 전송 실패
    @GetMapping("/notebox/sendFail")
    public String showSendNoteForm(Model model) {
        model.addAttribute("mpNoteboxWriteDTO", new MpNoteboxWriteDTO());
        return "mypage/mypage-notebox-write"; // 템플릿 경로
    }

    // 쪽지 보내기
    @PostMapping("/sendingNote")
    public String sendingNote(@ModelAttribute MpNoteboxWriteDTO mpNoteboxWriteDTO, RedirectAttributes redirectAttributes) {
        // 세션에서 회원 번호 가져오기
        Long senderNo = (Long) session.getAttribute("memberNo");

        if (senderNo == null) {
            redirectAttributes.addFlashAttribute("error", "발신자 정보를 찾을 수 없습니다.");
            return "redirect:/mypage/notebox/sendFail"; // 에러가 발생하면 다시 폼으로 돌아감
        }

        Long receiverNo;
        try {
            receiverNo = mpNoteBoxService.findMemberNoByNickname(mpNoteboxWriteDTO.getNoteboxReceiverName());
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            log.error("수신자 조회 실패: {}", e.getMessage());
            return "redirect:/mypage/notebox/sendFail"; // 에러가 발생하면 다시 폼으로 돌아감
        }

        if (receiverNo != null) {
            // DTO에 수신자 번호 및 발신자 번호 설정
            mpNoteboxWriteDTO.setNoteboxR(receiverNo);
            mpNoteboxWriteDTO.setNoteboxS(senderNo); // 발신자 번호 설정

            // 쪽지 전송
            mpNoteBoxService.sendingNote(mpNoteboxWriteDTO);
            log.info("쪽지가 성공적으로 전송되었습니다: {}", mpNoteboxWriteDTO);
            return "redirect:/mypage/noteboxSendList"; // 보낸 쪽지함으로 리다이렉트
        } else {
            redirectAttributes.addFlashAttribute("error", "회원 번호를 찾을 수 없습니다.");
            log.error("회원 번호를 찾을 수 없습니다: {}", mpNoteboxWriteDTO.getNoteboxReceiverName());
            return "redirect:/mypage/notebox/sendFail"; // 에러가 발생하면 다시 폼으로 돌아감
        }
    }


    @GetMapping("/noteboxDelete")
    public String deleteNote(@RequestParam("noteboxReceiveNo") Long noteboxReceiveNo, Model model) {
        boolean isDeleted = mpNoteBoxService.deleteNoteByReceiveNo(noteboxReceiveNo);

        if (isDeleted) {
            log.info("쪽지 번호 {}가 성공적으로 삭제되었습니다.", noteboxReceiveNo);
            return "redirect:/mypage/noteboxReceiveList"; // 쪽지 리스트 페이지로 리다이렉트
        } else {
            log.error("쪽지 번호 {} 삭제 실패", noteboxReceiveNo);
            model.addAttribute("errorMessage", "쪽지 삭제에 실패했습니다.");
            return "mypage/noteboxError"; // 에러 페이지로 이동
        }
    }

    @GetMapping("/updateProtectRequest")
    public String updateProtectRequest(@RequestParam("protectRequestNo") Long protectRequestNo, Model model) {
        MpProtectRequestDTO protectRequest = mypageService.protectRequestInfo(protectRequestNo);
        Long memberNo = (Long) session.getAttribute("memberNo");
        Long centerMemberNo = (Long) session.getAttribute("centerMemberNo");

        model.addAttribute("memberNo", memberNo);
        model.addAttribute("centerMemberNo", centerMemberNo);

        model.addAttribute("protectRequest", protectRequest);
        return "mypage/mypage-protect-form";

    }
    @PostMapping("/updateProtectRequestOk")
    public String updateProtectRequestOk(@RequestParam("protectRequestNo") Long protectRequestNo, MpProtectRequestDTO mpProtectRequestDTO, Model model) {
        MpProtectRequestDTO protectRequest = mypageService.protectRequestInfo(protectRequestNo);
        model.addAttribute("protectRequest", protectRequest);

        mypageService.updateProtectRequest(mpProtectRequestDTO);

        return "redirect:/mypage/updateProtectRequest?protectRequestNo=" + protectRequestNo;
    }

    @GetMapping("/updateAdoptRequest")
    public String updateAdoptRequest(@RequestParam("adoptRequestNo") Long adoptRequestNo, Model model) {
        MpAdoptRequestDTO adoptRequest = mypageService.adoptRequestInfo(adoptRequestNo);
        Long memberNo = (Long) session.getAttribute("memberNo");
        Long centerMemberNo = (Long) session.getAttribute("centerMemberNo");

        model.addAttribute("memberNo", memberNo);
        model.addAttribute("centerMemberNo", centerMemberNo);

        model.addAttribute("adoptRequest", adoptRequest);
        return "mypage/mypage-adopt-form";
    }

    @PostMapping("/updateAdoptRequestOk")
    public String updateAdoptRequestOk(@RequestParam("adoptRequestNo") Long adoptRequestNo, MpAdoptRequestDTO mpAdoptRequestDTO, Model model) {
        MpAdoptRequestDTO adoptRequest = mypageService.adoptRequestInfo(adoptRequestNo);
        model.addAttribute("adoptRequest", adoptRequest);

        mypageService.updateAdoptRequest(mpAdoptRequestDTO);

        return "redirect:/mypage/updateAdoptRequest?adoptRequestNo=" + adoptRequestNo;
    }

    @GetMapping("/updateVolunRequest")
    public String updateVolunRequest(@RequestParam("volunRequestNo") Long volunRequestNo, Model model) {
        MpVolunRequestDTO volunRequest = mypageService.volunRequestInfo(volunRequestNo);
        Long memberNo = (Long) session.getAttribute("memberNo");
        Long centerMemberNo = (Long) session.getAttribute("centerMemberNo");

        model.addAttribute("memberNo", memberNo);
        model.addAttribute("centerMemberNo", centerMemberNo);

        model.addAttribute("volunRequest", volunRequest);
        return "mypage/mypage-volun-form";
    }

    @PostMapping("/updateVolunRequestOk")
    public String updateVolunRequestOk(@RequestParam("volunRequestNo") Long volunRequestNo, MpVolunRequestDTO mpVolunRequestDTO, Model model) {
        MpVolunRequestDTO volunRequest = mypageService.volunRequestInfo(volunRequestNo);
        model.addAttribute("volunRequest", volunRequest);

        mypageService.updateVolunRequest(mpVolunRequestDTO);

        return "redirect:/mypage/updateVolunRequest?volunRequestNo=" + volunRequestNo;
    }

    @GetMapping("/withdrawal")
    public String withdrawal(Model model) {
        Long memberNo = (Long) session.getAttribute("memberNo");
        if (memberNo == null) {
            return "redirect:/login";
        }

        return "mypage/mypage-quit";
    }

    @PostMapping("/withdrawalOK")
    public String withdrawalOK() {
        Long memberNo = (Long) session.getAttribute("memberNo");

        // 세션이 만료된 경우 로그인 페이지로 리다이렉트
        if (memberNo == null) {
            return "redirect:/login";
        }

        // 탈퇴 로직을 서비스에서 처리
        boolean isWithdrawal = mypageService.withdrawal(memberNo);

        if (isWithdrawal) {
            session.invalidate(); // 세션 무효화
            log.info("회원 탈퇴가 완료되었습니다. 회원 번호 : {}", memberNo);
            return "redirect:/main/main"; // 메인 페이지로 리다이렉트
        } else {
            log.error("탈퇴 처리 중 오류 발생. 회원 번호 : {}", memberNo);
            return "redirect:/login"; // 오류 발생 시 로그인 페이지로 리다이렉트
        }
    }


}
