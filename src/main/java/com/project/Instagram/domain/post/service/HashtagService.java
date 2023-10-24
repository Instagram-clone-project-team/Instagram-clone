package com.project.Instagram.domain.post.service;

import com.project.Instagram.domain.post.entity.Hashtag;
import com.project.Instagram.domain.post.entity.Post;
import com.project.Instagram.domain.post.entity.PostHashtag;
import com.project.Instagram.domain.post.repository.HashtagRepository;
import com.project.Instagram.domain.post.repository.PostHashtagRepository;
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

    @Transactional
    public void registerHashtags(Post post){
        registerHashTag(post, post.getContent());
    }


    public void registerHashTag(Post post, String content){
        final Set<String> tagsOnContent = filteringHashtag(content);
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
    public void editHashTag(Post post, String beforeContent){
        final Set<String> afterNames = filteringHashtag(post.getContent());
        final Set<String> beforeNames = filteringHashtag(beforeContent);

        final Map<String, Hashtag> afterhashtagMap = hashtagRepository.findByTagNameIn(afterNames).stream()
                .collect(Collectors.toMap(Hashtag::getTagName, hashtag -> hashtag));
        final Map<String, Hashtag> beforeHashtagMap = hashtagRepository.findByTagNameIn(beforeNames).stream()
                .collect(Collectors.toMap(Hashtag::getTagName, hashtag -> hashtag));
        beforeNames.forEach(tagName -> {
            filteringAndDeleteHashtags(afterhashtagMap, beforeHashtagMap, tagName,post);
        });
        afterNames.forEach(tagName -> {
            filteringAndSaveHashtags(post, afterhashtagMap, beforeHashtagMap, tagName);
        });
    }

    private void filteringAndSaveHashtags(Post post, Map<String, Hashtag> afterhashtagMap, Map<String, Hashtag> beforeHashtagMap, String tagName) {
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

    private void filteringAndDeleteHashtags(Map<String, Hashtag> afterhashtagMap, Map<String, Hashtag> beforeHashtagMap, String tagName, Post post) {
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

    public Set<String> filteringHashtag(String content){
        Set<String> hashtags = new HashSet<>();
        final String regex = "#[0-9a-zA-Z가-힣ㄱ-ㅎ_]+";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matching = pattern.matcher(content);

        while(matching.find()){
            hashtags.add(matching.group().substring(1));
        }
        return new HashSet<>(hashtags);
    }

}
