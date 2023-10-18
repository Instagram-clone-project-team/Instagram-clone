package com.project.Instagram.domain.post.service;

import com.project.Instagram.domain.post.entity.CommentHashtag;
import com.project.Instagram.domain.post.entity.Hashtag;
import com.project.Instagram.domain.post.entity.Post;
import com.project.Instagram.domain.post.entity.PostHashtag;
import com.project.Instagram.domain.post.repository.CommentHashtagRepository;
import com.project.Instagram.domain.post.repository.HashtagRepository;
import com.project.Instagram.domain.post.repository.PostHashtagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HashtagService {
    private final HashtagRepository hashtagRepository;
    private final PostHashtagRepository postHashtagRepository;
    private final CommentHashtagRepository commentHashtagRepository;

    @Transactional
    public void registerHashtagsOnPost(Post post){
        registerHashTagOnPost(post, post.getContent());
    }
    @Transactional
    public void registerHashtagsOnComment(Comment comment){registerHashTagOnComment(comment,comment.getText())}
    @Transactional
    public void registerHashTagOnComment(Comment comment, String text ){
        final Set<String> tagsOnText = filteringHashtag(text);
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
            CommentHashtagRepository.save(new CommentHashtag(tempHashtag, comment));
        });
    }
    @Transactional
    public void editHashTag(Comment comment, String beforeContent){
        final Set<String> afterTags = filteringHashtag(comment.getText());
        final Set<String> beforeTags = filteringHashtag(beforeContent);

        final Map<String, Hashtag> afterHashtagMap = hashtagRepository.findByTagNameIn(aftertags).stream()
                .collect(Collectors.toMap(Hashtag::getTagName, hashtag -> hashtag));
        final Map<String, Hashtag> beforeHashtagMap = hashtagRepository.findByTagNameIn(beforeTags).stream()
                .collect(Collectors.toMap(Hashtag::getTagName, hashtag -> hashtag));
        List<Hashtag> deleteHashtags = new ArrayList<>();
        beforeTags.forEach(tagName -> {
            filteringBeforeHashtags(afterHashtagMap, beforeHashtagMap, deleteHashtags, tagName);
        });
        filteringAfterHashtagOnComment(comment, afterTags, afterHashtagMap, beforeHashtagMap);

        deleteHashtags.forEach(hashtag -> {
            CommentHashtag commentHashtag =commentHashtagRepository.findByPostAndHashtag(hashtag, comment);
            commentHashtag.setDeletedAt(LocalDateTime.now());
        });
    }



    @Transactional
    public void registerHashTagOnPost(Post post, String content){
        final Set<String> tagsOnContent = filteringHashtag(content);
        final Map<String, Hashtag> hashtagMap = hashtagRepository.findByTagNameIn(tagsOnContent).stream()
                .collect(Collectors.toMap(Hashtag::getTagName, hashtag -> hashtag));
        tagsOnContent.forEach(tagName -> {
            Hashtag tempHashtag;
            if(hashtagMap.containsKey(tagName)){//해시태그 있는경우
                tempHashtag=hashtagMap.get(tagName);
                tempHashtag.updatecount(1);
            }
            else{//없는경우
                tempHashtag = hashtagRepository.save(new Hashtag(tagName));
            }

            postHashtagRepository.save(new PostHashtag(tempHashtag,post));
        });
    }
    @Transactional
    public void editHashTag(Post post, String beforeContent){
        final Set<String> afterNames = filteringHashtag(post.getContent());
        final Set<String> beforeNames = filteringHashtag(beforeContent);

        final Map<String, Hashtag> hashtagMap = hashtagRepository.findByTagNameIn(afterNames).stream()
                .collect(Collectors.toMap(Hashtag::getTagName, hashtag -> hashtag));
        final Map<String, Hashtag> beforeHashtagMap = hashtagRepository.findByTagNameIn(beforeNames).stream()
                .collect(Collectors.toMap(Hashtag::getTagName, hashtag -> hashtag));
        final List<Hashtag> deleteHashtags =new ArrayList<>();
        beforeNames.forEach(tagName -> {
            filteringBeforeHashtags(hashtagMap, beforeHashtagMap, deleteHashtags, tagName);
        });
        afterNames.forEach(tagName -> {
            filteringAfterHashtagsOnPost(post, hashtagMap, beforeHashtagMap, tagName);
        });
        deleteAfterHashtagsInPost(post, deleteHashtags);

    }

    private void deleteAfterHashtagsInPost(Post post, List<Hashtag> deleteHashtags) {
        deleteHashtags.forEach(hashtag -> {
            PostHashtag postHashtag =postHashtagRepository.findByPostAndHashtag(hashtag, post);
            postHashtag.setDeletedAt(LocalDateTime.now());
        });
    }

    private void filteringAfterHashtagsOnPost(Post post, Map<String, Hashtag> hashtagMap, Map<String, Hashtag> beforeHashtagMap, String tagName) {
        if(beforeHashtagMap.containsKey(tagName)){
            return;
        }
        Hashtag tempHashtag;
        if(hashtagMap.containsKey(tagName)){//해시태그 있는경우
            tempHashtag= hashtagMap.get(tagName);
            if(tempHashtag.getCount()==0){
                tempHashtag.setDeletedAt(null);

            }
            tempHashtag.updatecount(1);
        }
        else{
            tempHashtag = hashtagRepository.save(new Hashtag(tagName));
        }
        postHashtagRepository.save(new PostHashtag(tempHashtag, post));
    }

    private void filteringBeforeHashtags(Map<String, Hashtag> hashtagMap, Map<String, Hashtag> beforeHashtagMap, List<Hashtag> deleteHashtags, String tagName) {
        if(hashtagMap.containsKey(tagName)){
            return;
        }
        Hashtag tempHashtag= beforeHashtagMap.get(tagName);
        if (tempHashtag.getCount() == 1) {
            tempHashtag.updatecount(-1);
            tempHashtag.setDeletedAt(LocalDateTime.now());
            deleteHashtags.add(tempHashtag);
        } else {
            tempHashtag.updatecount(-1);
            deleteHashtags.add(tempHashtag);
        }
    }
    private void filteringAfterHashtagOnComment(Comment comment, Set<String> afterTags, Map<String, Hashtag> afterHashtagMap, Map<String, Hashtag> beforeHashtagMap) {
        afterTags.forEach(tagName -> {
            if(beforeHashtagMap.containsKey(tagName)){
                return;
            }
            Hashtag tempHashtag;
            if(afterHashtagMap.containsKey(tagName)){//해시태그 있는경우
                tempHashtag= afterHashtagMap.get(tagName);
                if(tempHashtag.getCount()==0){
                    tempHashtag.setDeletedAt(null);

                }
                tempHashtag.updatecount(1);
            }
            else{
                tempHashtag = hashtagRepository.save(new Hashtag(tagName));
            }
            commentHashtagRepository.save(new CommentHashtag(tempHashtag, comment));
        });
    }
    public Set<String> filteringHashtag(String content){
        Set<String> hashtags = new HashSet<>();
        final String regex = "#[0-9a-zA-Z가-힣ㄱ-ㅎ_]+";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matching = pattern.matcher(content);

        while(matching.find()){
            hashtags.add(matching.group().substring(1));//#제외
        }
        return new HashSet<>(hashtags);
    }

}
