package ua.roffus.yooki.utils;

import ua.roffus.yooki.Game;

public class Constant {

    public static class Directions {
        public static final int LEFT = 0;
        public static final int RIGHT = 1;
    }

    public static class StateButtons {
        public static final int BUTTON_WIDTH_DEFAULT = 140;
        public static final int BUTTON_HEIGHT_DEFAULT = 55;
        public static final int BUTTON_WIDTH = (int) (BUTTON_WIDTH_DEFAULT * Game.SCALE * 2.5);
        public static final int BUTTON_HEIGHT = (int) (BUTTON_HEIGHT_DEFAULT * Game.SCALE * 2.5);
    }

    public static class Sounds {
        public static final int BACK = 0;
        public static final int ATTACK = 1;
        public static final int HIT = 2;
        public static final int GAMEOVER = 3;
        public static final int CLICK = 4;
        public static final int JUMP = 5;
    }

    public static class EnemyConstants {

        public static final int SCORPIO = 0;

        public static final int IDLE = 0;
        public static final int RUNNING = 1;
        public static final int ATTACK = 2;
        public static final int DEATH = 3;
        public static final int HIT = 4;

        public static final int SCORPIO_WIDTH_DEFAULT = 76;
        public static final int SCORPIO_HEIGHT_DEFAULT = 70;
        public static final int SCORPIO_WIDTH = (int) (SCORPIO_WIDTH_DEFAULT * Game.SCALE);
        public static final int SCORPIO_HEIGHT = (int) (SCORPIO_HEIGHT_DEFAULT * Game.SCALE);

        public static final int SCORPIO_DRAWOFFSET_X = (int) (6 * Game.SCALE);
        public static final int SCORPIO_DRAWOFFSET_Y = (int) (38 * Game.SCALE);

        public static int getSpriteAmount(int enemyType, int enemyState) {
            switch (enemyType) {
                case SCORPIO:
                    switch (enemyState) {
                        case IDLE:
                        case ATTACK:
                        case RUNNING:
                        case DEATH:
                            return 4;
                        case HIT:
                            return 2;
                    }
            }
            return 0;
        }
        public static int GetMaxHealth(int enemy_type) {
            switch (enemy_type) {
                case SCORPIO:
                    return 20;
                default:
                    return 1;
            }
        }

        public static int GetEnemyDamage(int enemy_type) {
            switch (enemy_type) {
                case SCORPIO:
                    return 15;
                default:
                    return 0;
            }
        }
    }

    public static class PlayerConstants {

        public static final int IDLE = 0;
        public static final int RUNNING = 1;
        public static final int JUMPING = 2;
        public static final int FALLING = 3;
        public static final int ATTACK1 = 4;
        public static final int HIT = 5;
        public static final int DEATH = 6;

        public static int getSpriteAmount(int action) {
            switch (action) {
                case RUNNING:
                case DEATH:
                    return 10;
                case IDLE:
                    return 8;
                case JUMPING:
                case HIT:
                    return 3;
                case FALLING:
                    return 4;
                case ATTACK1:
                    return 6;
                default:
                    return 1;
            }
        }
    }
}
