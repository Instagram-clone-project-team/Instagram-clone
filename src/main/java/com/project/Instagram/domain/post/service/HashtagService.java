package com.project.Instagram.domain.post.service;

import com.project.Instagram.domain.comment.entity.Comment;
import com.project.Instagram.domain.comment.entity.CommentHashtag;
import com.project.Instagram.domain.comment.repository.CommentHashtagRepository;
import com.project.Instagram.domain.post.entity.Hashtag;
import com.project.Instagram.domain.post.entity.Post;
import com.project.Instagram.domain.post.entity.PostHashtag;
import com.project.Instagram.domain.post.repository.HashtagRepository;
import com.project.Instagram.domain.post.repository.PostHashtagRepository;
import com.project.Instagram.global.util.StringExtractUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HashtagService {
    private final HashtagRepository hashtagRepository;
    private final PostHashtagRepository postHashtagRepository;
    private final StringExtractUtil stringExtractUtil;
    private final CommentHashtagRepository commentHashtagRepository;


    @Transactional
    public void registerHashTagOnComment(Comment comment, String content ){
        final Set<String> tagsOnText = stringExtractUtil.filteringHashtag(content);
        final Map<String, Hashtag> hashtagMap = hashtagRepository.findByTagNameIn(tagsOnText).stream()
                .collect((Collectors.toMap(Hashtag::getTagName, hashtag -> hashtag)));
        tagsOnText.forEach(tagName ->{
            Hashtag tempHashtag;
            if(hashtagMap.containsKey(tagName)){
                tempHashtag=hashtagMap.get(tagName);
                tempHashtag.updatecount(1);
            }
            else {
                tempHashtag = hashtagRepository.save(new Hashtag(tagName));
            }
            commentHashtagRepository.save(new CommentHashtag(tempHashtag, comment));
        });
    }

    @Transactional
    public void registerHashTagOnPost(Post post, String content){
        final Set<String> tagsOnContent = stringExtractUtil.filteringHashtag(content);
        final Map<String, Hashtag> hashtagMap = hashtagRepository.findByTagNameIn(tagsOnContent).stream()
                .collect(Collectors.toMap(Hashtag::getTagName, hashtag -> hashtag));
        tagsOnContent.forEach(tagName -> {
            Hashtag tempHashtag;
            if(hashtagMap.containsKey(tagName)){
                tempHashtag=hashtagMap.get(tagName);
                tempHashtag.updatecount(1);
            }
            else{
                tempHashtag = hashtagRepository.save(new Hashtag(tagName));
            }

            postHashtagRepository.save(new PostHashtag(tempHashtag,post));
        });
    }
    @Transactional
    public void editHashTagOnComment(Comment comment, String beforeContent){
        final Set<String> afterTags = stringExtractUtil.filteringHashtag(comment.getText());
        final Set<String> beforeTags = stringExtractUtil.filteringHashtag(beforeContent);

        final Map<String, Hashtag> afterHashtagMap = hashtagRepository.findByTagNameIn(afterTags).stream()
                .collect(Collectors.toMap(Hashtag::getTagName, hashtag -> hashtag));
        final Map<String, Hashtag> beforeHashtagMap = hashtagRepository.findByTagNameIn(beforeTags).stream()
                .collect(Collectors.toMap(Hashtag::getTagName, hashtag -> hashtag));
        beforeTags.forEach(tagName -> {
            deleteHashtagsOnComment(afterHashtagMap, beforeHashtagMap, tagName,comment);
        });
        afterTags.forEach(tagName -> {
            saveHashtagsOnComment(afterHashtagMap, beforeHashtagMap,tagName,comment);
        });

    }
    @Transactional
    public void editHashTagOnPost(Post post, String beforeContent){
        final Set<String> afterNames = stringExtractUtil.filteringHashtag(post.getContent());
        final Set<String> beforeNames = stringExtractUtil.filteringHashtag(beforeContent);
        final Map<String, Hashtag> afterhashtagMap = hashtagRepository.findByTagNameIn(afterNames).stream()
                .collect(Collectors.toMap(Hashtag::getTagName, hashtag -> hashtag));
        final Map<String, Hashtag> beforeHashtagMap = hashtagRepository.findByTagNameIn(beforeNames).stream()
                .collect(Collectors.toMap(Hashtag::getTagName, hashtag -> hashtag));
        beforeNames.forEach(tagName -> {
            deleteHashtagsOnPost(afterhashtagMap, beforeHashtagMap, tagName,post);
        });
        afterNames.forEach(tagName -> {
            saveHashtagsOnPost(afterhashtagMap, beforeHashtagMap, tagName,post);
        });
    }
    @Transactional
    private void saveHashtagsOnPost(Map<String, Hashtag> afterhashtagMap, Map<String, Hashtag> beforeHashtagMap, String tagName, Post post) {
        if(beforeHashtagMap.containsKey(tagName)){
            return;
        }
        Hashtag tempHashtag;
        if(afterhashtagMap.containsKey(tagName)){
            tempHashtag= afterhashtagMap.get(tagName);
            tempHashtag.updatecount(1);
        }
        else{
            tempHashtag = hashtagRepository.save(new Hashtag(tagName));
        }
        postHashtagRepository.save(new PostHashtag(tempHashtag, post));
    }
    @Transactional
    private void saveHashtagsOnComment(Map<String, Hashtag> afterhashtagMap, Map<String, Hashtag> beforeHashtagMap, String tagName, Comment comment) {
        if(beforeHashtagMap.containsKey(tagName)){
            return;
        }
        Hashtag tempHashtag;
        if(afterhashtagMap.containsKey(tagName)){
            tempHashtag= afterhashtagMap.get(tagName);
            tempHashtag.updatecount(1);
        }
        else{
            tempHashtag = hashtagRepository.save(new Hashtag(tagName));
        }
        commentHashtagRepository.save(new CommentHashtag(tempHashtag, comment));
    }
    @Transactional
    private void deleteHashtagsOnPost(Map<String, Hashtag> afterhashtagMap, Map<String, Hashtag> beforeHashtagMap, String tagName, Post post) {
        if(afterhashtagMap.containsKey(tagName)){
            return;
        }
        Hashtag tempHashtag= beforeHashtagMap.get(tagName);
        PostHashtag postHashtag = postHashtagRepository.findByPostAndHashtag(tempHashtag,post);
        if (tempHashtag.getCount() == 1) {
            postHashtagRepository.delete(postHashtag);
            hashtagRepository.delete(tempHashtag);
        } else {
            tempHashtag.updatecount(-1);
            postHashtagRepository.delete(postHashtag);
        }
    }
    @Transactional
    private void deleteHashtagsOnComment(Map<String, Hashtag> afterhashtagMap, Map<String, Hashtag> beforeHashtagMap, String tagName, Comment comment) {
        if(afterhashtagMap.containsKey(tagName)){
            return;
        }
        Hashtag tempHashtag= beforeHashtagMap.get(tagName);
        CommentHashtag commentHashtag = commentHashtagRepository.findByHashtagAndComment(tempHashtag,comment);
        if (tempHashtag.getCount() == 1) {
            commentHashtagRepository.delete(commentHashtag);
            hashtagRepository.delete(tempHashtag);
        } else {
            tempHashtag.updatecount(-1);
            commentHashtagRepository.delete(commentHashtag);
        }
    }
}
