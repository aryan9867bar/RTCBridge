package com.example.webrtc;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.Map;

@Controller
public class MainController {

    ArrayList<String> users = new ArrayList<String>();

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String Index() {
        return "index";
    }

    @MessageMapping("/testServer")
    @SendTo("/topic/testServer")
    public String testServer(String Test) {
        System.out.println("Testing Server");
        return Test;
    }

    @MessageMapping("/reject")
    public void handleRejectCall(@Payload Map<String, String> payload) {
        String toUser = payload.get("toUser");
        String fromUser = payload.get("fromUser");
        simpMessagingTemplate.convertAndSendToUser(toUser, "/topic/reject", fromUser + " has rejected your call.");
    }

    @MessageMapping("/hangup")
    public void handleHangup(@Payload HangupRequest request) {
        String toUser = request.getToUser();
        String fromUser = request.getFromUser();
        simpMessagingTemplate.convertAndSendToUser(toUser, "/topic/hangup", fromUser + " has ended the call.");
    }

    private static class UserRequest {
        public String username;
        public String clientId;
    }

    @MessageMapping("/addUser")
    public void addUser(UserRequest request) {
        String response;
        if (users.contains(request.username)) {
            response = "User already exists: " + request.username;
        } else {
            users.add(request.username);
            response = "success";
        }
        System.out.println(response);
        simpMessagingTemplate.convertAndSend(
            "/topic/" + request.username + "/" + request.clientId + "/userResponse",
            response
        );
    }

    @MessageMapping("/call")
    public void Call(String call) {
        JSONObject jsonObject = new JSONObject(call);
        System.out.println("Calling to: " + jsonObject.get("callTo") + " Call from " + jsonObject.get("callFrom"));
        simpMessagingTemplate.convertAndSendToUser(jsonObject.getString("callTo"), "/topic/call", jsonObject.get("callFrom"));
    }

    @MessageMapping("/disconnectUser")
    public void disconnectUser(DisconnectRequest request) {
        users.remove(request.getUsername());
        System.out.println("User disconnected: " + request.getUsername());
    }

    private static class DisconnectRequest {
        private String username;
        private String clientId;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getClientId() { return clientId; }
        public void setClientId(String clientId) { this.clientId = clientId; }
    }

    @MessageMapping("/offer")
    public void Offer(String offer) {
        System.out.println("Offer Came");
        JSONObject jsonObject = new JSONObject(offer);
        simpMessagingTemplate.convertAndSendToUser(jsonObject.getString("toUser"), "/topic/offer", offer);
        System.out.println("Offer Sent");
    }

    @MessageMapping("/answer")
    public void Answer(String answer) {
        System.out.println("Answer came");
        JSONObject jsonObject = new JSONObject(answer);
        simpMessagingTemplate.convertAndSendToUser(jsonObject.getString("toUser"), "/topic/answer", answer);
        System.out.println("Answer Sent");
    }

    @MessageMapping("/candidate")
    public void Candidate(String candidate) {
        System.out.println("Candidate came");
        JSONObject jsonObject = new JSONObject(candidate);
        simpMessagingTemplate.convertAndSendToUser(jsonObject.getString("toUser"), "/topic/candidate", candidate);
        System.out.println("Candidate Sent");
    }
}
