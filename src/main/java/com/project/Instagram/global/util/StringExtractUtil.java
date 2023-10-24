package com.project.Instagram.global.util;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class StringExtractUtil {

    public List<String> filteringMentions(String content) {
        List<String> mention_usernames = new ArrayList<>();
        String regex = "@[0-9a-zA-Z가-힣ㄱ-ㅎ_]+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matching = pattern.matcher(content);

        while (matching.find()) {
            mention_usernames.add(matching.group().substring(1));
        }
        return mention_usernames;
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
