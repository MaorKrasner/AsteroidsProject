package krasner.maor.asteroids.clientserver;

import krasner.maor.asteroids.game.Game;
import lombok.extern.slf4j.Slf4j;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

@Slf4j
public class Sender extends KeyAdapter {
    int lastKeyCodePressed;

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE)
            MyClient.out.println("pressedSpace " + Game.singlePlayer.x + " " + Game.singlePlayer.y);
        else if (isNewKeyCode(e.getKeyCode()))
            MyClient.out.println("keyCodePressed " + e.getKeyCode());
    }


    public void keyReleased(KeyEvent e) {
        MyClient.out.println("keyCodeReleased " + e.getKeyCode());
        lastKeyCodePressed = -1; // the next key will always be new
    }

    boolean isNewKeyCode(int keyCode) {
        boolean ok = keyCode != lastKeyCodePressed;
        lastKeyCodePressed = keyCode;
        return ok;
    }
}
