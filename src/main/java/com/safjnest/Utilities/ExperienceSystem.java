package com.safjnest.Utilities;

import java.util.Random;

/**
 * This class is used to manage the experience system of the bot.
 * <p>
 * The experience system is used to give experience to the users of the bot when they send a message in a server every one minute.
 */
public class ExperienceSystem {

    public static final int NOT_LEVELED_UP = -1;

    private static final int MAX_EXPERIENCE = 25;
    private static final int MIN_EXPERIENCE = 15;
    
    /**
     * This method is used to calculate the experience that the user will receive.
     * <p> The experience is calculated randomly between {@link #MIN_EXPERIENCE} and {@link #MAX_EXPERIENCE}.
     * @return
     */
    private static int getRandomExp(){
        return new Random().nextInt((MAX_EXPERIENCE - MIN_EXPERIENCE) + 1) + MAX_EXPERIENCE;
    }


    /**
     * This method is used to calculate the total experience that the user needs to
     * get to be level {@code lvl} from zero.
     * <p>
     * The experience is calculated using the following formula:
     * {@code (5/6) * (lvl) * (2 * (lvl) * (lvl) + 27 * (lvl) + 91)}.
     * <table border="2">
     * <tr>
     * <td>LVL EXP</td>
     * </tr>
     * <tr>
     * <td>1 100</td>
     * </tr>
     * <tr>
     * <td>2 255</td>
     * </tr>
     * <tr>
     * <td>3 475</td>
     * </table>
     * @param lvl
     * @return
     */
    public static int getExpToReachLvlFromZero(int lvl){
        return (int) ((5.0/6.0) * (lvl) * (2 * (lvl) * (lvl) + 27 * (lvl) + 91));
    }

    public static int getExpToReachLvl(int lvl){
        return ExperienceSystem.getExpToReachLvlFromZero(lvl + 1) - ExperienceSystem.getExpToReachLvlFromZero(lvl);
    }


    /**
     * This method is used to calculate the level you would be with a given quantity of exp.
     * @param exp
     * @return 
     */
    private static double getLevelFromExp(double exp) {
        double epsilon = 1e-6;
        double lvl = 0.0;
        while (true) {
            double equationValue = (5.0 / 3.0) * Math.pow(lvl, 3) + (45.0 / 2.0) * Math.pow(lvl, 2) + (455.0 / 6.0) * lvl;

            if (Math.abs(equationValue - exp) < epsilon) {
                return lvl;
            }

            double derivativeValue = (5.0) * Math.pow(lvl, 2) + (45.0) * lvl + (455.0 / 6.0);
            lvl -= (equationValue - exp) / derivativeValue;
        }
    }


    /**
     * This method is used to calculate the experience that the user needs to get level up.
     * <p>
     * So if the user is level 1 with 175 exp and to get level 2 needs a total of 255 experience, this method will return 80.
     * @param lvl
     * @param exp
     * @return 
     */
    public static int getExpToLvlUp(int lvl, int exp){
        if(lvl == 1 && exp < 100)
            lvl = 0;
        return (exp - getExpToReachLvlFromZero(lvl));
    }

    public static int getLvlUpPercentage(int lvl, int exp) {
        return Math.round((float)ExperienceSystem.getExpToLvlUp(lvl, exp)/(float)(getExpToReachLvl(lvl))*100);
    }


    public static int calculateExp(int exp, double modifer) {
        return exp + Math.round((float) ((double) getRandomExp() * modifer));
    }

    public static int isLevelUp(int exp, int level) {
        int newLvl = (int) getLevelFromExp(exp);
        if (newLvl > level) {
            return newLvl;
        }
        return NOT_LEVELED_UP;
    }
}