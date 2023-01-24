package krasner.maor.asteroids.clientserver;

import krasner.maor.asteroids.game.Game;
import krasner.maor.asteroids.objects.Player;
import krasner.maor.asteroids.util.Constants;

public class Receiver extends Thread {
    Player p;

    Player fromWhichPlayerIs(int id) {
        if (id == MyClient.id)
            return Game.singlePlayer;
        else if (id == (MyClient.id+1)% Constants.MAX_PLAYERS)
            return Game.singlePlayer;
        return null;
    }

    public void run() {
        String str;

        while (MyClient.in.hasNextLine()) {
            this.p = fromWhichPlayerIs(MyClient.in.nextInt()); // id of client
            str = MyClient.in.next();

            if (str.equals("newPoint")) {
                //this.p = fromWhichPlayerIs(Client.in.nextInt()); // PROBLEM HERE
                for (int i = 0; i < 3; i++)
                    p.polygon.xpoints[i] = MyClient.in.nextInt(); // problem here
                for (int i = 0; i < 3; i++)
                    p.polygon.ypoints[i] = MyClient.in.nextInt(); // problem
                p.game.repaint();
                //SharedPanel.player1.mp.repaint();
            }
            //else if (str.equals("playerJoined")) {
           //     p.connected = true;
          //  }
            //else if (str.equals("newStatus")) {
            //    p.sc.setLoopStatus(Client.in.next());
            //}
            //else if (str.equals("stopStatusUpdate")) {
           //     p.sc.stopLoopStatus();
           // }
        }
        MyClient.in.close();
    }
}
