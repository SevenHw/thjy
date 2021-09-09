package com.tanhua.server.controller;

import com.tanhua.model.dto.RecommendUserDto;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.TodayBest;
import com.tanhua.server.service.TanhuaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tanhua")
public class TanhuaController {
    @Autowired
    private TanhuaService tanhuaService;

    /**
     * 今日佳人
     */
    @GetMapping("todayBest")
    public ResponseEntity todayBest() {
        TodayBest vo = tanhuaService.todayBest();
        return ResponseEntity.ok(vo);
    }

    /**
     * 分页查询好友推荐列表
     * /tanhua/recommendation
     */
    @GetMapping("/recommendation")
    private ResponseEntity recommendationRes(RecommendUserDto dto) {
        PageResult pr = tanhuaService.recommendation(dto);
        return ResponseEntity.ok(pr);
    }

    /**
     * 查询佳人信息
     * /tanhua/:id/personalInfo
     *
     * @param userId
     * @return
     */
    @GetMapping("/{id}/personalInfo")
    public ResponseEntity personalInfo(@PathVariable("id") Long userId) {
        TodayBest pr = tanhuaService.personalInfo(userId);
        return ResponseEntity.ok(pr);
    }

    /**
     * 查看陌生人问题
     * /tanhua/strangerQuestions
     */
    @GetMapping("/strangerQuestions")
    public ResponseEntity strangerQuestions(Long userId) {
        String pr = tanhuaService.strangerQuestions(userId);
        return ResponseEntity.ok(pr);
    }

    /**
     * 回复陌生人问题
     * /tanhua/strangerQuestions
     */
    @PostMapping("/strangerQuestions")
    public ResponseEntity strangerQues(@RequestBody Map map) {
        String userIdString = map.get("userId").toString();
        Long userId = Long.valueOf(userIdString);
        String reply = (String) map.get("reply");
        tanhuaService.strangerQues(userId, reply);
        return ResponseEntity.ok(null);
    }

    /**
     * 探花-左滑右滑
     * /tanhua/cards
     */
    @GetMapping("cards")
    public ResponseEntity cards() {
        List<TodayBest> tb = tanhuaService.cards();
        return ResponseEntity.ok(tb);
    }

    /**
     * 探花-喜欢
     * /tanhua/:id/love
     */
    @GetMapping("{id}/love")
    public ResponseEntity love(@PathVariable("id") Long userId) {
        tanhuaService.love(userId);
        return ResponseEntity.ok(null);
    }

    /**
     * 探花-不喜欢
     * /tanhua/:id/unlove
     */
    @GetMapping("{id}/unlove")
    public ResponseEntity unlove(@PathVariable("id") Long userId) {
        tanhuaService.unlove(userId);
        return ResponseEntity.ok(null);
    }

}
