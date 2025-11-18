import dataaccess.DataAccessException;
import dataaccess.SQLDataAccess;
import server.Server;

public class ServerMain {
    public static void main(String[] args) throws DataAccessException {
        Server server = new Server();
        server.run(8080);
        System.out.println("♕ 240 Chess Server");
    }
}