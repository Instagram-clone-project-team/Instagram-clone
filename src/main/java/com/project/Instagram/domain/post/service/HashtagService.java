package com.project.Instagram.domain.post.service;

import com.project.Instagram.domain.post.entity.Hashtag;
import com.project.Instagram.domain.post.entity.Post;
import com.project.Instagram.domain.post.entity.PostHashtag;
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

        final Map<String, Hashtag> hashtagMap = hashtagRepository.findByTagNameIn(afterNames).stream()
                .collect(Collectors.toMap(Hashtag::getTagName, hashtag -> hashtag));
        final Map<String, Hashtag> beforeHashtagMap = hashtagRepository.findByTagNameIn(beforeNames).stream()
                .collect(Collectors.toMap(Hashtag::getTagName, hashtag -> hashtag));
        final List<Hashtag> deleteHashtags =new ArrayList<>();
        beforeNames.forEach(tagName -> {
            filteringBeforeHashtags(hashtagMap, beforeHashtagMap, deleteHashtags, tagName);
        });
        afterNames.forEach(tagName -> {
            filteringAfterHashtags(post, hashtagMap, beforeHashtagMap, tagName);
        });
        deleteAfterHashtags(post, deleteHashtags);

    }

    private void deleteAfterHashtags(Post post, List<Hashtag> deleteHashtags) {
        deleteHashtags.forEach(hashtag -> {
            PostHashtag postHashtag =postHashtagRepository.findByPostAndHashtag(hashtag, post);
            postHashtag.setDeletedAt(LocalDateTime.now());
        });
    }

    private void filteringAfterHashtags(Post post, Map<String, Hashtag> hashtagMap, Map<String, Hashtag> beforeHashtagMap, String tagName) {
        if(beforeHashtagMap.containsKey(tagName)){
            return;
        }
        Hashtag tempHashtag;
        if(hashtagMap.containsKey(tagName)){
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

    private static void filteringBeforeHashtags(Map<String, Hashtag> hashtagMap, Map<String, Hashtag> beforeHashtagMap, List<Hashtag> deleteHashtags, String tagName) {
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
