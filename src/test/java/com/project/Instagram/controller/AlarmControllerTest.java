package com.project.Instagram.controller;

import com.project.Instagram.domain.alarm.controller.AlarmController;
import com.project.Instagram.domain.alarm.service.AlarmService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static com.project.Instagram.global.response.ResultCode.GET_ALARMS_SUCCESS;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(AlarmController.class)
@MockBean(JpaMetamodelMappingContext.class)
public class AlarmControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    AlarmService alarmService;


    @Test
    @DisplayName(" 알람 불러오기 테스트")
    @WithMockUser
    public  void getAlarmsPage() throws Exception{
        int page = 1;
        int size = 5;

        mockMvc.perform(get("/alarms")
                        .param("page", "1")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(GET_ALARMS_SUCCESS.getStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(GET_ALARMS_SUCCESS.getMessage()));

        verify(alarmService).getAlarms(page-1,size);
    }
}