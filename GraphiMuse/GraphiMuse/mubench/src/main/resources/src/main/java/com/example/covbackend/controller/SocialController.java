package com.example.covbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.covbackend.annotations.UserLoginToken;
import com.example.covbackend.common.Result;
import com.example.covbackend.entity.Comment;
import com.example.covbackend.entity.CovData;
import com.example.covbackend.entity.Question;
import com.example.covbackend.entity.User;
import com.example.covbackend.service.CommentService;
import com.example.covbackend.service.QuestionService;
import com.example.covbackend.service.UserService;
import com.example.covbackend.util.TokenUtil;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import java.text.SimpleDateFormat;
import java.util.*;

@RequestMapping("/social")
@RestController
public class SocialController {
    @Resource
    QuestionService questionService;
    @Resource
    CommentService commentService;
    @Resource
    UserService userService;

    @UserLoginToken
    @RequestMapping("/publishquestion")
    public Result publishQuestion(@RequestBody Map map){
        int id = Integer.parseInt(TokenUtil.getTokenUserId());

        String title = map.get("title").toString();
        String content = map.get("content").toString();

        if(title==null) return Result.fail("问题标题不能为空");

        Question question = new Question();
        question.setUserId(id);
        question.setTitle(title);
        question.setContent(content);
        Date date = new Date();
        question.setTime(date);

        try{
            questionService.save(question);
        }catch (Exception e){
            return Result.fail("发布问题失败,请重试");
        }

        return Result.succ();
    }

    @RequestMapping("/showquestionbykey")
    public Result showQuestionByKeyword(@RequestBody Map map){
        String keyword = map.get("keyword").toString();
        List<HashMap<String,Object>> questionList = questionService.getQuestionByKeyword(keyword);

        if(questionList==null) return Result.fail("未找到与您搜索有关的问题");

        HashMap<String,Object> data = new HashMap<>();
        data.put("questionlist",questionList);
        return Result.succ(data);
    }

    @RequestMapping("/shownewquestion")
    public Result showNewQuestion(@RequestBody Map map){
        List<HashMap<String,Object>> questionList = questionService.getNewestQuestion();

        if(questionList==null) return Result.fail("问题列表为空");

        HashMap<String,Object> data = new HashMap<>();
        data.put("questionlist",questionList);
        return Result.succ(data);
    }

    @RequestMapping("showquestiondetail")
    public Result showQuestionDetail(@RequestBody Map map){
        int id = Integer.parseInt(map.get("question_id").toString());
        Question question = new Question();
        try {
            question = questionService.getById(id);
        }catch(Exception e){
            return Result.fail("问题已被删除");
        }

        User u1;
        try{
            u1 = userService.getUserById(question.getUserId());
        }catch (Exception e){
            return Result.fail("获取提问者失败");
        }

        List<Comment> commentList = new ArrayList<>();
        try{
            commentList = commentService.getCommentByQuestionId(id);
        }catch(Exception e){
            return Result.fail("查看评论失败,请刷新");
        }

        List<HashMap<String,Object>> comments = new ArrayList<>();

        for(int i=0; i<commentList.size(); i++){
            HashMap<String,Object> one_comment = new HashMap<>();
            User u = userService.getById(commentList.get(i).getUserId());
            one_comment.put("name",u.getUsername());
            one_comment.put("content",commentList.get(i).getContent());
            one_comment.put("time",commentList.get(i).getTime());
            one_comment.put("img","");
            one_comment.put("introduction",u.getIntroduction());
            comments.add(one_comment);
        }

        HashMap<String,Object> data = new HashMap<>();
        data.put("question",question);
        data.put("username",u1.getUsername());
        data.put("comments",comments);
        return Result.succ(data);
    }

    @UserLoginToken
    @RequestMapping("/myquestion")
    public Result myQuestion(@RequestBody Map map){
        int id = Integer.parseInt(TokenUtil.getTokenUserId());

        List<Question> questionList = new ArrayList<>();
        try{
            questionList = questionService.getQuestionByUserId(id);
        }catch(Exception e){
            return Result.fail("获取个人问题失败");
        }
        //System.out.println(questionList);
        HashMap<String,Object> data = new HashMap<>();
        data.put("questionlist", questionList);
        return Result.succ(data);

    }

    @RequestMapping("/deletequestion")
    public Result deleteQuestion(@RequestBody Map map){
        int id = Integer.parseInt(map.get("question_id").toString());
        if(questionService.removeById(id)){
            commentService.removeCommentsByQuestionId(id);
            return Result.succ();
        }
        return Result.fail("删除问题失败");
    }

    @UserLoginToken
    @RequestMapping("publishcomment")
    public Result publishComment(@RequestBody Map map){
        int id = Integer.parseInt(TokenUtil.getTokenUserId());
        int question_id = Integer.parseInt(map.get("question_id").toString());
        String content = map.get("content").toString();

        Comment comment = new Comment();
        comment.setUserId(id);
        comment.setQuestionId(question_id);
        comment.setContent(content);
        Date date = new Date();
        comment.setTime(date);

        try{
            commentService.save(comment);
        }catch(Exception e){
            return Result.fail("发表评论失败，请重试");
        }

        return Result.succ();
    }

    @UserLoginToken
    @RequestMapping("mycomment")
    public Result myComment(@RequestBody Map map){
        int id = Integer.parseInt(TokenUtil.getTokenUserId());
        //System.out.println("userid: " + id);
        List<Comment> commentList;
        List<Map<String, Object>> list = new ArrayList<>();
        try{
            commentList = commentService.getCommentByUserId(id);
            //System.out.println(commentList);
            for(Comment comment : commentList){
                int question_id = comment.getQuestionId();
                Question question = questionService.getById(question_id);
                User user = userService.getUserById(question.getUserId());
                Map<String, Object> tempmap = new HashMap<>();
                tempmap.put("questiontitle", question.getTitle());
                tempmap.put("questionuser", user.getUsername());
                tempmap.put("id", comment.getId());
                tempmap.put("content", comment.getContent());
                tempmap.put("time", comment.getTime());
                list.add(tempmap);
            }
        }catch(Exception e){
            System.out.println(e);
            return Result.fail("获取个人评论失败");
        }

        HashMap<String,Object> data = new HashMap<>();
        data.put("list", list);
        return Result.succ(data);
    }

    @RequestMapping("/deletecomment")
    public Result deleteComment(@RequestBody Map map){
        int id = Integer.parseInt(map.get("comment_id").toString());
        if(commentService.removeById(id)){
            return Result.succ();
        }
        return Result.fail("删除评论失败");
    }

    @RequestMapping("/searchquestion")
    public Result searchQuestion(@RequestBody Map map){
        String keyword = map.get("keyword").toString();
        List<HashMap<String,Object>> questionList = new ArrayList<>();

        try{
            questionList = questionService.getQuestionByKeyword(keyword);
        }catch (Exception e){
            return Result.fail("未找到相关结果");
        }

        //System.out.println(questionList);
        HashMap<String,Object> data = new HashMap<>();
        data.put("questionlist", questionList);

        return Result.succ(data);
    }



}
