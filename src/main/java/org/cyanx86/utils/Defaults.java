package org.cyanx86.utils;

import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.Sound;

public class Defaults {

    public static class GeneralSettings {
        public static String language = "en-us";
        public static String invalid_message_path = "No defined message in path '%path%.'";
    }

    public static class RoundSettings {

        public static class GameRound {
            public static int startCountdown = 3;
            public static int roundTime = 270;
            public static int endIntermission = 3;
            public static int playerImmobilization = 3;
        }

        public static class ScoreManager {
            public static int valuePerOrder = 30;
        }

        public static class OrderManager {
            public static int timeForNextOrder = 20;
            public static int orderTimeOut = 80;
            public static int orderStackLimit = 5;
            public static float bonusProbability = 0.2f;
        }

    }

    public static class RoundSounds {
        public static Instrument countDownInstrument = Instrument.BIT;
        public static Note countDownTone = new Note(1, Note.Tone.F, true);
        public static Note startTone = new Note(2, Note.Tone.F, true);

        public static Sound timeRunningOutSound = Sound.BLOCK_ANVIL_PLACE;
        public static Sound finishSound = Sound.BLOCK_ANVIL_USE;

        public static Sound orderEntrySound = Sound.BLOCK_AMETHYST_CLUSTER_HIT;
        public static Sound deliveredOrderSound = Sound.ENTITY_PLAYER_LEVELUP;
        public static Sound lostOrderSound = Sound.BLOCK_ENCHANTMENT_TABLE_USE;
    }

}
