package com.project.Instagram.domain.post.service;

import com.project.Instagram.domain.post.entity.Hashtag;
import com.project.Instagram.domain.post.entity.Post;
import com.project.Instagram.domain.post.entity.PostHashtag;
import com.project.Instagram.domain.post.repository.HashtagRepository;
import com.project.Instagram.domain.post.repository.PostHashtagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
        final Set<PostHashtag> postHashtags = new HashSet<>();
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
