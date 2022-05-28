package ua.roffus.yooki.utils;

import ua.roffus.yooki.Game;

import java.awt.geom.Rectangle2D;

public class Handler {

    //Checking if the entity hitbox is hitting the texture
    public static boolean CanMoveHere(float x, float y, float width, float height, int[][] lvlData) {
        if (!IsSolid(x, y, lvlData))
            if (!IsSolid(x + width, y + height, lvlData))
                if (!IsSolid(x + width, y, lvlData))
                    return !IsSolid(x, y + height, lvlData);
        return false;
    }

    //Checking if there is a texture at a point
    private static boolean IsSolid(float x, float y, int[][] lvlData) {
        int maxWidth = lvlData[0].length * Game.TILES_SIZE;
        if (x < 0 || x >= maxWidth)
            return true;
        if (y < 0 || y >= Game.GAME_HEIGHT)
            return true;

        float xIndex = x / Game.TILES_SIZE;
        float yIndex = y / Game.TILES_SIZE;

        return IsTileSolid((int) xIndex, (int) yIndex, lvlData);
    }

    public static boolean IsTileSolid(int xTile, int yTile, int[][] lvlData) {
        int value = lvlData[yTile][xTile];

        return value >= 72 || value < 0 || value != 16;
    }

    public static float GetEntityXPosNextToWall(Rectangle2D.Float hitBox, float xSpeed) {
        int currentTile = (int) (hitBox.x / Game.TILES_SIZE);
        if (xSpeed > 0) {
            //go right
            int tileXPos = currentTile * Game.TILES_SIZE;
            int xOffset = (int) (Game.TILES_SIZE - hitBox.width);
            return tileXPos + xOffset - 1;
        } else
            //go left
            return currentTile * Game.TILES_SIZE;
    }

    public static float GetEntityYPosUnderRoofOrAboveFloor(Rectangle2D.Float hitBox, float airSpeed) {
        int currentTile = (int) (hitBox.y / Game.TILES_SIZE);
        if (airSpeed > 0) {
            // Falling
            int tileYPos = currentTile * Game.TILES_SIZE;
            int yOffset = (int) (Game.TILES_SIZE - hitBox.height);
            return tileYPos + yOffset - 1;
        } else
            // Jumping
            return currentTile * Game.TILES_SIZE;
    }

    //check pixel left and bottom right. if there are no pixels -> entity is in the air
    public static boolean IsEntityOnFloor(Rectangle2D.Float hitBox, int[][] lvlData) {
        if (!IsSolid(hitBox.x, hitBox.y + hitBox.height + 1, lvlData))
            return IsSolid(hitBox.x + hitBox.width, hitBox.y + hitBox.height + 1, lvlData);
        return true;
    }

    public static boolean IsFloor(Rectangle2D.Float hitBox, float xSpeed, int[][] lvlData) {
        return IsSolid(hitBox.x + xSpeed, hitBox.y + hitBox.height + 1, lvlData);
    }

    public static boolean IsAllTileWalkable(int xStart, int xEnd, int y, int[][] lvlData) {
        for (int i = 0; i < xEnd - xStart; i++) {
            if (IsTileSolid(xStart + i, y, lvlData))
                return false;
            if(!IsTileSolid(xStart + i, y + 1, lvlData))
                return false;
        }
        return true;
    }

    //checking if there is anything in the way of the entity to the player. if not -> go to him
    public static boolean IsSightClear(int[][] lvlData, Rectangle2D.Float enemyHitBox,
                                       Rectangle2D.Float playerHitBox, int tileY) {
        int enemyXTile = (int) (enemyHitBox.x / Game.TILES_SIZE);
        int playerXTile = (int) (playerHitBox.x / Game.TILES_SIZE);

        if (enemyXTile > playerXTile)
            return IsAllTileWalkable(playerXTile, enemyXTile, tileY, lvlData);
        else
            return IsAllTileWalkable(enemyXTile, playerXTile, tileY, lvlData);
    }
}
