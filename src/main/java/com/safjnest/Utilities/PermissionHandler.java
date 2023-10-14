package com.safjnest.Utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

/**
 * This class handles all matters related to discord permissions.
 * It also stores the discord tags of the members that have special permissions.
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * @since 1.0
 */
public class PermissionHandler {
    /**
     * This list contains the discord tags of the authors of the bot.
     * <ul>
     * <li>Leon412</li>
     * <li>NeutronSun</li>
     * </ul>
     */
    private static final Set<String> untouchables = Set.of("383358222972616705", "440489230968553472");

    /**
     * Discord tag of Merio epria
     */
    private static final String epria = "707479292644163604";

    public static Set<String> getUntouchables() {
        return untouchables;
    }
    
    /**
     * Check if a discord tag is in the list of {@link com.safjnest.Utilities.PermissionHandler#untouchables untouchables}
     * 
     * @param id Discord tag of the member
     * @return True if the member is in the list, false otherwise
     */
    public static boolean isUntouchable(String id) {
        if (untouchables.contains(id))
            return true;
        return false;
    }

    /**
     * Check if a member is Epria
     * @param id Discord tag of the member
     * @return True if the member is Epria, false otherwise
     */
    public static boolean isEpria(String id) {
        if (epria.equals(id))
            return true;
        return false;
    }

    /**
     * @return Epria's discord tag
     */
    public static String getEpria(){
        return epria;
    }

    /**
     * Gets the value of the important permissions.
     * @return The long rappresentation of the permissions that are important for the bot
     */
    public static long getImportantPermissionsValue(){ 
        return Permission.getRaw(Permission.MANAGE_CHANNEL, Permission.NICKNAME_CHANGE, 
        Permission.MANAGE_SERVER, Permission.MANAGE_ROLES, Permission.MANAGE_PERMISSIONS, 
        Permission.MANAGE_WEBHOOKS, Permission.VIEW_AUDIT_LOGS,
        Permission.MODERATE_MEMBERS, Permission.MESSAGE_MANAGE, Permission.MESSAGE_MENTION_EVERYONE, 
        Permission.PRIORITY_SPEAKER, Permission.ADMINISTRATOR);
    }

    /**
     * Get all the permissions of a member in the guild that the command is executed in.
     * @param member Member to check
     * @return List of permission names
     */
    public static List<String> getPermissionNames(Member member){
        List<String> finalPermissions= new ArrayList<String>();
        for (Permission permission : member.getPermissions())
            finalPermissions.add(permission.getName());
        return finalPermissions;
    }

    /**
    * Get the filtered permissions of a member in the guild that the command is executed in.
    * <p>Only takes the permissions that are returned from {@link com.safjnest.Utilities.PermissionHandler#getImportantPermissionsValue getImportantPermissionsValue}
    * @param member Member to check
    * @return List of permission names
    */
    public static List<String> getFilteredPermissionNames(Member member) {
        List<String> finalPermissions= new ArrayList<String>();
        for (Permission permission : member.getPermissions())
            if(Permission.getPermissions(getImportantPermissionsValue()).contains(permission))
                finalPermissions.add(permission.getName());
        return finalPermissions;
    }

    /**
     * 
     * @param roles
     * @return
     */
    public static List<String> getMaxFieldableRoleNames(List<Role> roles) {
        return getMaxFieldableRoleNames(roles, 1024);
    }

    /**
     * Gets the maximum number of roles that can fit in an embed's field 
     * and makes a list with their names omitting the ones that would make the field too long.
     * @param roles List of roles
     * @param charNumber Maximum number of characters
     * @return List of roles names
     */
    public static List<String> getMaxFieldableRoleNames(List<Role> roles, int charNumber) {
        if(charNumber > 1024)
            throw new IllegalArgumentException("il numero dei caratteri non puo' essere maggiore di 1024");
        List<String> finalRoles = new ArrayList<String>();
        int rolesLenght = 0, countSpaces;
        List<Character> toDelete = Arrays.asList((char)8291, (char)8194, (char)32); //Only deletes them if they are at the very beginning/end of the role name
        for (int i = 0; i < roles.size(); i++) {
            String role = roles.get(i).getName();

            countSpaces = 0;
            while(toDelete.contains(role.charAt(countSpaces)))
                countSpaces++;
            if(countSpaces > 0){
                role = role.substring(countSpaces);
                countSpaces = 0;
            }
            while(toDelete.contains(role.charAt(role.length()-countSpaces-1)))
                countSpaces++;
            if(countSpaces > 0)
                role = role.substring(0, role.length()-countSpaces);

            rolesLenght += role.length() + 2; //accounts for the comma and space put by the toString method of the final List
            if(rolesLenght >= charNumber)
                break;
            
            finalRoles.add(role);
        }
        return finalRoles;
    }

    public static List<String> getMaxFieldableUserNames(List<Member> users, int charNumber) {
        if(charNumber > 1024)
            throw new IllegalArgumentException("il numero dei caratteri non puo' essere maggiore di 1024");
        List<String> finalUser = new ArrayList<String>();
        int usersLenght = 0, countSpaces;
        List<Character> toDelete = Arrays.asList((char)8291, (char)8194, (char)32); //Only deletes them if they are at the very beginning/end of the role name
        for (int i = 0; i < users.size(); i++) {
            String user = users.get(i).getEffectiveName();

            countSpaces = 0;
            while(toDelete.contains(user.charAt(countSpaces)))
                countSpaces++;
            if(countSpaces > 0){
                user = user.substring(countSpaces);
                countSpaces = 0;
            }
            while(toDelete.contains(user.charAt(user.length()-countSpaces-1)))
                countSpaces++;
            if(countSpaces > 0)
                user = user.substring(0, user.length()-countSpaces);

                usersLenght += user.length() + 2; //accounts for the comma and space put by the toString method of the final List
            if(usersLenght >= charNumber)
                break;
            
            finalUser.add(user);
        }
        return finalUser;
    }

    /**
     * Checks if a member has a specific permission.
     * <p>If the member is in the untouchables list, it returns true.
     * @param theGuy Member to check
     * @param permission Permission to check
     * @return True if the member has the permission, false otherwise
     */
    public static boolean hasPermission(Member theGuy, Permission permission) {
        if (theGuy.hasPermission(permission) || untouchables.contains(theGuy.getId()))
            return true;
        return false;
    }

    public static User getMentionedUser(CommandEvent event, String name){
        User user = null;
        if(event.getMessage().getMentions().getMembers().size() > 0)
            user = event.getMessage().getMentions().getMembers().get(0).getUser();
        else
            try {
                user = event.getJDA().retrieveUserById(name).complete();
            } catch (Exception e) {}
        return user;
    }

    public static Member getMentionedMember(CommandEvent event, String name){
        Member member = null;
        if(event.getMessage().getMentions().getMembers().size() > 0)
            member = event.getMessage().getMentions().getMembers().get(0);
        else
            try {
                member = event.getGuild().getMemberById(name);
            } catch (Exception e) {}
        return member;
    }

    public static Guild getGuild(CommandEvent event, String guildName) {
        Guild guild = null;
        try {
            guild = event.getJDA().getGuildById(guildName);
        }
        catch (Exception e) {}
        return guild;
    }

    public static boolean isUserBanned(Guild guild, User user) throws InsufficientPermissionException{
        try {
            guild.retrieveBan(user).complete();
            return true;
        } catch (ErrorResponseException e) {
            return false;
        }
    }
}