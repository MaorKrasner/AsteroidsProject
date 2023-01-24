package krasner.maor.asteroids.clientserver;

import java.awt.event.KeyEvent;

public class CoordinatesController extends Thread {
    boolean up, right, left, down;
    int id;

    public CoordinatesController(int id) {
        this.id = id;
        up = right = left = down = false;
    }

    void keyCodePressed(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
                up = true; down = right = left = false;
                //ClientManager.sendToAllClients(this.id + " newStatus up");
                break;
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                down = true; up = right = left = false;
                //ClientManager.sendToAllClients(this.id + " newStatus down");
                break;
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                right = true; up = down = left = false;
                //ClientManager.sendToAllClients(this.id + " newStatus right");
                break;
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
                left = true; up = down = right = false;
                //ClientManager.sendToAllClients(this.id + " newStatus left");
                break;
        }
    }

    void keyCodeReleased(int keyCode) {
        if (keyCode != KeyEvent.VK_W && keyCode != KeyEvent.VK_S && keyCode != KeyEvent.VK_D && keyCode != KeyEvent.VK_A)
            return;

        //ClientManager.sendToAllClients(this.id + " stopStatusUpdate");
        switch (keyCode) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                up = false;
                break;
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                down = false;
                break;
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                right = false;
                break;
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
                left = false;
                break;
        }
    }

    public void run() {
        int[] newX = MyServer.players[id].p.xpoints;
        int[] newY = MyServer.players[id].p.ypoints;

        while (true) {
            if (up || down || right || left) {
                if (up)   {
                   for (int i = 0; i < 3; i++)
                       newY[i] = MyServer.players[id].p.ypoints[i] - 10;
                    /*
                    if (id == 0) {
                        SharedPanel.player1.direction = 1;
                        SharedPanel.player1.move();
                    }
                    else if (id == 1) {
                        SharedPanel.player2.direction = 1;
                        SharedPanel.player2.move();
                    }
                    else {
                        SharedPanel.player3.direction = 1;
                        SharedPanel.player2.move();
                    }
                    */
                }
                else if (down) {
                    for (int i = 0; i < 3; i++)
                        newY[i] = MyServer.players[id].p.ypoints[i] + 10;
                    /*
                    if (id == 0) {
                        SharedPanel.player1.direction = -1;
                        SharedPanel.player1.move();
                    }
                    else if (id == 1) {
                        SharedPanel.player2.direction = -1;
                        SharedPanel.player2.move();
                    }
                    else {
                        SharedPanel.player3.direction = -1;
                        SharedPanel.player2.move();
                    }
                    */
                }
                else if (right) {
                    for (int i = 0; i < 3; i++)
                        newX[i] = MyServer.players[id].p.xpoints[i] + 10;
                    /*
                    if (id == 0) {
                        SharedPanel.player1.rotateRight();
                        SharedPanel.player1.rotateByAcceleration();
                    }
                    else if (id == 1) {
                        SharedPanel.player2.rotateRight();
                        SharedPanel.player2.rotateByAcceleration();
                    }
                    else {
                        SharedPanel.player3.rotateRight();
                        SharedPanel.player3.rotateByAcceleration();
                    }
                    */
                }
                else if (left)  {
                    for (int i = 0; i < 3; i++)
                        newX[i] = MyServer.players[id].p.xpoints[i] - 10;
                    /*
                    if (id == 0) {
                        SharedPanel.player1.rotateLeft();
                        SharedPanel.player1.rotateByAcceleration();
                    }
                    else if (id == 1) {
                        SharedPanel.player2.rotateLeft();
                        SharedPanel.player2.rotateByAcceleration();
                    }
                    else {
                        SharedPanel.player3.rotateLeft();
                        SharedPanel.player3.rotateByAcceleration();
                    }
                    */
                }

                if (coordinateIsValid(newX, newY)) {
                    //for (int i = 0; i < 3; i++)
                    ClientManager.sendToAllClients(id + " newPoint " + newX[0] + " " + newY[0]);
                    ClientManager.sendToAllClients( " " + newX[1] + " " + newY[1]);
                    ClientManager.sendToAllClients( " " + newX[2] + " " + newY[2]);
                    MyServer.players[id].p.xpoints = newX;
                    MyServer.players[id].p.ypoints = newY;
                } else {
                    newX = MyServer.players[id].p.xpoints;
                    newY = MyServer.players[id].p.ypoints;
                }
                try {
                    sleep(5);
                } catch (InterruptedException e) {}
            }
            try {sleep(0);} catch (InterruptedException e) {}
        }
    }

    public boolean coordinateIsValid(int[] x, int[] y) {
        for (int i = 0; i < 3; i++) {
            if (!(x[i] > 50 && x[i] < 1200 && y[i] > 30 && y[i] < 690))
                return false;
        }
        return true;
    }
}
