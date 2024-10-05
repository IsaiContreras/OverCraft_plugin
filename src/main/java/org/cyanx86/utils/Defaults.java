package org.cyanx86.utils;

public class Defaults {

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

}
