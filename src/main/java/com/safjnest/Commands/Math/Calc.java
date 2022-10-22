package com.safjnest.Commands.Math;

//import java.math.BigInteger;
import java.util.Set;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.CommandsHandler;
import com.safjnest.Utilities.SafJNest;

/**
 * This class is used to calculate the result of a mathematical expression.
 * <p>There is one way to use it:</p>
 * By typing <code>%calc</code> followed by the expression you want to calculate.
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * 
 * @since 1.2.5
 */
public class Calc extends Command{
    /**
     * A set of all the operators that can be used in a mathematical expression.
     */
    private static final Set<String> OP = Set.of("+","-","*","/", "^");

    public Calc(){
        this.name = this.getClass().getSimpleName();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
    }

    @Override
    protected void execute(CommandEvent event) {
        String command = event.getArgs();
        try {
            if(Character.isDigit(command.charAt(0))){
                Double a = 0.0, b = 0.0;
                //BigInteger x = BigInteger.ZERO, y = BigInteger.ZERO;
                char sign = 'a';
                for(int i = 0; i < command.length(); i++){
                    if(OP.contains(String.valueOf(command.charAt(i)))){
                        a = Double.parseDouble(command.substring(0, i));
                        b = Double.parseDouble(command.substring(i+1));
                        sign = command.charAt(i);
                        break;
                    }
                }
                switch(sign){
                    case '+':
                        event.reply(String.valueOf((a+b)));
                        break;
    
                    case '-':
                        event.reply(String.valueOf((a-b)));
                        break;
    
                    case '*':
                        event.reply(String.valueOf((a*b)));
                        break;
    
                    case '/':
                        event.reply(String.valueOf((a/b)));
                        break;
                    
                    case '^':
                        event.reply(String.valueOf((Math.pow(a, b))));
                        break;
                }
            }else{
                String fun = "";
                Double a = 0.0;
                if(command.indexOf(" ") != -1){
                    for(int i = 0; i < command.length(); i++){
                        if(Character.isDigit(command.charAt(i))){
                            fun = command.substring(0, i-1);
                            a = Double.parseDouble(command.substring(i));
                            break;
                        }
                    }
                }else{
                    fun = command;
                }
                switch(fun){
                    case "ln":
                        event.reply(String.valueOf(Math.log(a)));
                        break;
                    case "sqrt":
                        event.reply(String.valueOf(Math.sqrt(a)));
                        break;
                    case "sin":
                        event.reply(String.valueOf(Math.sin(Math.toRadians(a))));
                        break;
                    case "cos":
                        event.reply(String.valueOf(Math.cos(Math.toRadians(a))));
                        break;
                    case "tan":
                        event.reply(String.valueOf(Math.tan(Math.toRadians(a))));
                        break;
                    case "acos":
                        event.reply(String.valueOf(Math.acos(a)));
                        break;
                    case "asin":
                        event.reply(String.valueOf(Math.asin(a)));
                        break;
                    case "atan":
                        event.reply(String.valueOf(Math.atan(a)));
                        break;
                    case "exp":
                        event.reply(String.valueOf(Math.exp(a)));
                        break;
                    case "abs":
                        event.reply(String.valueOf(SafJNest.abs(a)));
                        break;
                    case "ceil":
                        event.reply(String.valueOf(Math.ceil(a)));
                        break;
                    case "floor":
                        event.reply(String.valueOf(Math.floor(a)));
                        break;
                    case "round":
                        event.reply(String.valueOf(Math.round(a)));
                        break;
                    case "random":
                        event.reply(String.valueOf(Math.random()));
                        break;
                    case "pi":
                        event.reply(String.valueOf(Math.PI));
                        break;
                    case "e": 
                        event.reply(String.valueOf(Math.E));
                        break;
                    case "phi":
                        event.reply(String.valueOf("1.61803398874989484820458683436563811772030917980576286213544862270526046281890244970720720418939113748475408807538689175212663386222353693179318006076672610443393446812795873044892257869253112159134292237490748136420658817832414242428371683509571617250156327250701143849071502580791418140005204309324200449988123624083773577130"));
                        break;
                    case "euler":
                        event.reply(String.valueOf("0.577215664901532860606512090082402431042159335939923598805767234884867726777664670936947063291746749514631447249807082480960504014486542836224173997644923536253500333742937337737673942792595258242410821328572393355293033111103825178703854433910249678948246593850463699351535362078243194025960440768546997841981407328651952824240888367734335324547556805669122423471402146667663022193230540919037680597071758069097939139765441777293427451409845016711021194447154361244897"));
                        break;
                    case "catalan":
                        event.reply(String.valueOf("0.915965594177219015054603514932384110774149374281672134266498119621763019776254769479356512926115106248574422629495047237301373657403639846768002680697619341907348465243321073139718011615270971187257201985714771014745328262879429593924582154449576857260085837273330602458223172540207260489086245545492194917897393683334653371824594908648874446121897356857151445996796241464257518573575165312185711131715398938171519343915661462916443629463119436661345305877227879272965994341760399053635149980921577223636045291731644980709933244438684746138520474843693526262438865456718661557127213600988993160513160585482816686423587098239660288424087148209022362647461314032768291470348958702085308967232450483601"));
                        break;
                    default:
                        event.reply("Function not found, for other information use **help** command.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            event.reply("SYNTAX ERROR: " + e.getMessage());
        }
    }
}