package com.safjnest.Utilities.Controller.Interface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.safjnest.Bot;
import com.safjnest.Utilities.SQL.DatabaseHandler;
import com.safjnest.Utilities.SQL.QueryResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;



import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;



@RestController
@RequestMapping("/api")
public class ApiController {
    

    @Autowired
    public ApiController() { }

    @GetMapping("/guilds")
    public ResponseEntity<List<Map<String, String>>> getEmployeeByIdAndGuilds(@RequestBody List<String> ids) {
        JDA jda = Bot.getJDA();
        List<Map<String, String>> guilds = new ArrayList<>();
        for(String guildId : ids){
            try {
                Guild g = jda.getGuildById(guildId);
                Map<String, String> guildInfo = new HashMap<>();
                guildInfo.put("id", g.getId());
                guildInfo.put("name", g.getName());
                guildInfo.put("icon", g.getIconUrl());
                guilds.add(guildInfo);
            } catch (Exception ignored) { }
        }
        return ResponseEntity.ok(guilds);
    }

    @PostMapping("/{guildId}/prefix")
    public String setPrefix(@PathVariable String guildId, @RequestBody(required = false) String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prefix is required");
        }
        prefix = prefix.replace("\"", "");
        boolean response = Bot.getGuildData(guildId).setPrefix(prefix);
        String responseString = response ? "{\"status\":\"success\"}" : "{\"status\":\"error\"}";
        return responseString;
    }

    @GetMapping("/{guildId}")
    public ResponseEntity<Map<String, String>> getGuild(@PathVariable String guildId) {
        JDA jda = Bot.getJDA();
        Guild g = jda.getGuildById(guildId);
        if (g == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unvalid guild id. Try with another id.");
        }
        Map<String, String> guildInfo = new HashMap<>();
        guildInfo.put("id", g.getId());
        guildInfo.put("name", g.getName());
        guildInfo.put("icon", g.getIconUrl());
        return ResponseEntity.ok(guildInfo);
    }

    @GetMapping("{guildId}/users")
    public ResponseEntity<List<Map<String, String>>> getUsers(@PathVariable String guildId) {
        JDA jda = Bot.getJDA();
        Guild g = jda.getGuildById(guildId);
        if (g == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unvalid guild id. Try with another id.");
        }
        List<Map<String, String>> users = new ArrayList<>();
        for (net.dv8tion.jda.api.entities.Member m : g.getMembers()) {
            Map<String, String> userInfo = new HashMap<>();
            userInfo.put("id", m.getId());
            userInfo.put("nickname", m.getNickname());
            userInfo.put("name", m.getUser().getName());
            userInfo.put("icon", m.getUser().getAvatarUrl());
            users.add(userInfo);
        }

        return ResponseEntity.ok(users);
    }

    @GetMapping("/{guildId}/leaderboard")
    public ResponseEntity<List<Map<String, String>>> getLeaderboard(@PathVariable String guildId) {
        QueryResult leaderboard = DatabaseHandler.getUsersByExp(guildId, 0);
        if(leaderboard.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No results");
        }
        return ResponseEntity.ok(leaderboard.toList());
    }

}
