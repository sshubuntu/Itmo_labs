import Utility.AskBreak;
import Utility.StandardConsole;
import utility.Runner;

import java.net.UnknownHostException;

class Client {
    public static void main(String[] args) throws AskBreak {
        Runner runner = new Runner(new StandardConsole());
        runner.interactiveMode();
    }
}