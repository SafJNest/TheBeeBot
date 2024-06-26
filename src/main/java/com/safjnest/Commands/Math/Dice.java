package com.safjnest.Commands.Math;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.CommandsLoader;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.0
 */
public class Dice extends Command {

    private static final Pattern DICE_PATTERN = Pattern.compile("(\\d+)d(\\d+)");
    private static final Pattern MODIFIER_PATTERN = Pattern.compile("([+-/*])\\s*(\\d+)");
    private static final Pattern EXPLODE_PATTERN = Pattern.compile("e(\\d*)?");
    private static final Pattern REROLL_PATTERN = Pattern.compile("r(\\d*)");
    private static final Pattern TARGET_PATTERN = Pattern.compile("t(\\d+)");
    private static final Pattern FAILURE_PATTERN = Pattern.compile("f(\\d+)");
    private static final Pattern SETS_PATTERN = Pattern.compile("(\\d+)\\s+(\\d+)d(\\d+)");
    private static final Pattern UNROLL_PATTERN = Pattern.compile("(!\\s*)?(unsort|ul)");
    private static Random RANDOM = new Random();

    public Dice() {
        this.name = this.getClass().getSimpleName().toLowerCase();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
    }

    @Override
    protected void execute(CommandEvent event) {
        String command = event.getArgs();
        List<Integer> rolls = new ArrayList<>();
        int total = 0;
        boolean showTally = true;
        //boolean unsort = false;
        StringBuilder comment = new StringBuilder();
        int sets = 1;

        String[] parts = command.split("\\s+");
        for (int i = 0; i < parts.length; i++) {
            RANDOM = new Random(System.nanoTime());
            String part = parts[i];

            if (DICE_PATTERN.matcher(part).matches()) {
                // Rolling dice
                Matcher diceMatcher = DICE_PATTERN.matcher(part);
                if (diceMatcher.matches()) {
                    int diceCount = Integer.parseInt(diceMatcher.group(1));
                    int diceSides = Integer.parseInt(diceMatcher.group(2));
                    boolean explode = false;
                    int explodeValue = diceSides;
                    boolean reroll = false;
                    int rerollValue = 0;

                    // Checking for explode or reroll modifiers
                    if (i + 1 < parts.length) {
                        Matcher explodeMatcher = EXPLODE_PATTERN.matcher(parts[i + 1]);
                        if (explodeMatcher.matches()) {
                            explode = true;
                            if (!explodeMatcher.group(1).isEmpty()) {
                                explodeValue = Integer.parseInt(explodeMatcher.group(1));
                            }
                            i++;
                        } else {
                            Matcher rerollMatcher = REROLL_PATTERN.matcher(parts[i + 1]);
                            if (rerollMatcher.matches()) {
                                reroll = true;
                                if (!rerollMatcher.group(1).isEmpty()) {
                                    rerollValue = Integer.parseInt(rerollMatcher.group(1));
                                }
                                i++;
                            }
                        }
                    }

                    // Rolling the dice
                    int rollCount = 0;
                    while (rollCount < diceCount) {
                        System.out.println(diceSides);
                        int roll =  RANDOM.nextInt(diceSides) + 1;
                        if (explode && roll >= explodeValue) {
                            total += roll;
                            rolls.add(roll);
                            diceCount++;
                        } else if (reroll && roll <= rerollValue) {
                            roll =  RANDOM.nextInt(diceSides) + 1;
                            total += roll;
                            rolls.add(roll);
                        } else {
                            total += roll;
                            rolls.add(roll);
                            rollCount++;
                        }
                    }
                } else if (MODIFIER_PATTERN.matcher(part).matches()) {
                    // Applying modifier
                    Matcher modifierMatcher = MODIFIER_PATTERN.matcher(part);
                    if (modifierMatcher.matches()) {
                        char operator = modifierMatcher.group(1).charAt(0);
                        int operand = Integer.parseInt(modifierMatcher.group(2));
                        switch (operator) {
                            case '+':
                                total += operand;
                                break;
                            case '-':
                                total -= operand;
                                break;
                            case '*':
                                total *= operand;
                                break;
                            case '/':
                                total /= operand;
                                break;
                        }
                    }
                } else if (TARGET_PATTERN.matcher(part).matches()) {
                    // Checking target number
                    Matcher targetMatcher = TARGET_PATTERN.matcher(part);
                    if (targetMatcher.matches()) {
                        int targetNumber = Integer.parseInt(targetMatcher.group(1));
                        showTally = total >= targetNumber;
                    }
                } else if (FAILURE_PATTERN.matcher(part).matches()) {
                    // Checking failure number
                    Matcher failureMatcher = FAILURE_PATTERN.matcher(part);
                    if (failureMatcher.matches()) {
                        int failureNumber = Integer.parseInt(failureMatcher.group(1));
                        showTally = total < failureNumber;
                    }
                } else if (SETS_PATTERN.matcher(part).matches()) {
                    // Rolling multiple sets of dice
                    Matcher setsMatcher = SETS_PATTERN.matcher(part);
                    if (setsMatcher.matches()) {
                        int setCount = Integer.parseInt(setsMatcher.group(1));
                        int diceCount = Integer.parseInt(setsMatcher.group(2));
                        int diceSides = Integer.parseInt(setsMatcher.group(3));
                        for (int j = 0; j < setCount; j++) {
                            int setTotal = 0;
                            List<Integer> setRolls = new ArrayList<>();
                            for (int k = 0; k < diceCount; k++) {
                                int roll = RANDOM.nextInt(diceSides) + 1;
                                setTotal += roll;
                                setRolls.add(roll);
                            }
                            rolls.addAll(setRolls);
                            total += setTotal;
                        }
                    }
                } else if (UNROLL_PATTERN.matcher(part).matches()) {
                    // Unsort the list of rolls
                    Matcher unrollMatcher = UNROLL_PATTERN.matcher(part);
                    if (unrollMatcher.matches()) {
                        //unsort = true;
                    }
                } else {
                    // Adding comment
                    comment.append(part).append(" ");
                }
            }
        }
        
        // Displaying the result
        if (showTally) {
            event.reply("Rolls: " + rolls.toString());
            event.reply("Total: " + total);
            if (sets > 1) {
                event.reply("Sets: " + sets);
            }
            if (comment.length() > 0) {
                event.reply("Comment: " + comment.toString().trim());
            }
        } else {
            event.reply("Rolls: " + rolls.toString() + " (Hidden)\n" + "Total: " + total + " (Hidden)");
        }

    }

}
