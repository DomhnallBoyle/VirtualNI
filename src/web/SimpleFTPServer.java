package web;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

public class SimpleFTPServer 
{
	enum CMDS {
		CMD_USER,
		CMD_PASS,
		CMD_ACCT,
		CMD_CWD,
		CMD_CDUP,
		CMD_SMNT,
		CMD_QUIT,
		CMD_REIN,
		CMD_PORT,
		CMD_PASV,
		CMD_TYPE,
		CMD_STRU,
		CMD_MODE,
		CMD_RETR,
		CMD_STOR,
		CMD_STOU,
		CMD_APPE,
		CMD_ALLO,
		CMD_REST,
		CMD_RNFR,
		CMD_RNTO,
		CMD_ABOR,
		CMD_DELE,
		CMD_EPSV,
		CMD_RMD,
		CMD_MKD,
		CMD_PWD,
		CMD_LIST,
		CMD_NLST,
		CMD_SITE,
		CMD_SYST,
		CMD_STAT,
		CMD_HELP,
		CMD_NOOP,
		CMD_UNKNOWN,
		CMD_EMPTY,
		CMD_CLOSE
	};
	/**
	 * Response messages. 
	 */
	final String REPL_110 = "110 Restart marker reply.\r\n";
	final String REPL_120 = "120 Try again in 2 minutes.\r\n";
	final String REPL_125 = "125 Data connection already open; transfer starting.\r\n";
	final String REPL_150 = "150 File status okay; about to open data connection.\r\n";
	final String REPL_200 = "200 Command okay.\r\n";
	final String REPL_202 = "202 Command not implemented, superfluous at this site.\r\n";
	final String REPL_211 = "221 System status, or system help reply.\r\n";
	final String REPL_211_STATUS = "221-status of %s.\r\n";
	final String REPL_211_END = "221 End of status.\r\n";
	final String REPL_212 = "212 Directory status.\r\n";
	final String REPL_213 = "213 File status.\r\n";
	final String REPL_214 = "214 Help message.\r\n";
	final String REPL_214_END = "214 End Help message.\r\n";
	final String REPL_215 = "215 %s system type.\r\n";
	final String REPL_220 = "220 Service ready for new user.\r\n";
	final String REPL_221 = "221 Service closing control connection.\r\n";
	final String REPL_225 = "225 Data connection open; no transfer in progress.\r\n";
	final String REPL_226 = "226 Closing data connection.\r\n";
	final String REPL_227 = "227 Entering Passive Mode (%s,%s,%s,%s,%s,%s).\r\n";
	final String REPL_229 = "227 Entering Extended Passive Mode (|||%s|).\r\n";
	final String REPL_230 = "230 User logged in, proceed.\r\n";
	final String REPL_250 = "250 Requested file action okay, completed.\r\n";
	final String REPL_257 = "257 %s created.\r\n";
	final String REPL_257_PWD = "257 \"%s\" is current working dir.\r\n";
	final String REPL_331 = "331 Only anonymous user is accepted.\r\n";
	final String REPL_331_ANON = "331 Anonymous login okay, send your complete email as your password.\r\n";
	final String REPL_332 = "332 Need account for login.\r\n";
	final String REPL_350 = "350 Requested file action pending further information.\r\n";
	final String REPL_421 = "421 Service not available, closing control connection.\r\n";
	final String REPL_425 = "425 Can't open data connection.\r\n";
	final String REPL_426 = "426 Connection closed; transfer aborted.\r\n";
	final String REPL_450 = "450 Requested file action not taken.\r\n";
	final String REPL_451 = "451 Requested action aborted. Local error in processing.\r\n";
	final String REPL_452 = "452 Requested action not taken.\r\n";
	final String REPL_500 = "500 Syntax error, command unrecognized.\r\n";
	final String REPL_501 = "501 Syntax error in parameters or arguments.\r\n";
	final String REPL_502 = "502 Command not implemented.\r\n";
	final String REPL_503 = "503 Bad sequence of commands.\r\n";
	final String REPL_504 = "504 Command not implemented for that parameter.\r\n";
	final String REPL_530 = "530 Not logged in.\r\n";
	final String REPL_532 = "532 Need account for storing files.\r\n";
	final String REPL_550 = "550 Requested action not taken.\r\n";
	final String REPL_551 = "551 Requested action aborted. Page type unknown.\r\n";
	final String REPL_552 = "552 Requested file action aborted.\r\n";
	final String REPL_553 = "553 Requested action not taken.\r\n";

	public static String lastDir = "";

	public static void main(String[] list) throws Exception
	{
		SimpleFTPServer server = new SimpleFTPServer();
		//copts
		server.create_socket("~");
		
		while(true)
		{
			
		}
	}

	public SimpleFTPServer()
	{
	}
	
	public SimpleFTPServer(int p,int cp)
	{
		port = p;
		c_port = cp;
	}
	
//	int open_connections = 0;
	int port = 8081;
	int c_port = 8082;
	private ServerSocket ss = null;

	/**
	 * Creates new server listening socket and make the main loop , which waits
	 * for new connections.
	 */
	public void create_socket(final String root)//struct cmd_opts *opts) 
	{
		try
		{
			final ServerSocket ss = new ServerSocket( port );
			Thread t = new Thread( new Runnable()
				{
					public void run()
					{
						try
						{
							while( true )
								new FTPSession( ss.accept(), c_port++, root);
						}
						catch ( IOException ioe )
						{}
					}
				});
			t.setDaemon( true );
			t.start();
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
		}
//		if(opts==NULL)
//			return 10;
//		int status = chdir(opts->chrootdir);
//		if(status!=0) {
//			raiseerr(15);
//		}
//		int servaddr_len =  0;
//		int connection = 0;
//		int sock = 0;
//		int pid  = 0;
//		open_connections=0;
		
		//struct sockaddr_in servaddr;
		//pid = getuid();	
//		if(pid != 0 && opts->port <= 1024)
//		{
//			printf(_(" Access denied:\n     Only superuser can listen to ports (1-1024).\n You can use \"-p\" option to specify port, greater than 1024.\n"));
//			exit(1);
//		}
//		memset((char *)&servaddr, 0, sizeof(servaddr));
//		servaddr.sin_family = PF_INET;
//		if(opts->listen_any==true) {
//			servaddr.sin_addr.s_addr =  htonl(INADDR_ANY);
//		}
//		else if(opts->listen_addr==NULL) {
//			return 9;
//		} else {
//			struct hostent *host = gethostbyname(opts->listen_addr);
//			if(host==NULL) {
//				printf(_("Cannot create socket on server address: %s\n"),opts->listen_addr);
//				return 11;
//			}
//			bcopy(host->h_addr, &servaddr.sin_addr, host->h_length);
//		}
//		servaddr.sin_port = htons (opts->port);
//		servaddr_len = sizeof(servaddr);
//		if ((sock = socket (PF_INET, SOCK_STREAM, IPPROTO_TCP)) < 0) {
//			raiseerr(ERR_CONNECT);
//			return 1;
//		}
//		int flag = 1;
//		setsockopt(sock, SOL_SOCKET,SO_REUSEADDR,(char *) &flag, sizeof(int));
//		
//		// remove the Nagle algorhytm, which improves the speed of sending data.
//		setsockopt(sock, IPPROTO_TCP,TCP_NODELAY,(char *) &flag, sizeof(int));
//		
//		if(bind (sock, (struct sockaddr *)&servaddr, sizeof(servaddr))<0) {
//			if(opts->listen_any==false) {
//				printf(_("Cannot bind address: %s\n"),opts->listen_addr);
//			}else {
//				printf(_("Cannot bind on default address\n"));
//			}
//			return raiseerr(8);
//		}
//		if(listen(sock,opts->max_conn) <0) {
//			return raiseerr(2);
//		}
//		#ifdef __USE_GNU
//			signal(SIGCHLD, (sighandler_t )sig_chld_handler);
//		#endif
//		#ifdef __USE_BSD
//			signal(SIGCHLD, (sig_t )sig_chld_handler);
//		#endif

//		ss = new ServerSocket( port );
//		
//		for (;;) 
//		{
			//max_limit_notify = false;
//			ss.accept();
			
//			if ((connection = accept(sock, (struct sockaddr *) &servaddr, &servaddr_len)) < 0) {
//				raiseerr(3);
//				return -1;
//			}
//			
//			pid = fork();
//			if(pid==0) {
//				if(open_connections >= opts->max_conn)
//					max_limit_notify=true;
//				interract(connection,opts);
//			} else if(pid>0) {
//				open_connections++;
//				assert(close_connection(connection)>=0);
//			}
//			else {
//				 
//				close(connection);
//				close(sock);
//				assert(0);
//			}
//		}
	}

	private class FTPSession implements Runnable
	{
		boolean DEBUG = true;
		Socket mySocket = null;
		BufferedWriter writer = null;//new BufferedReader( new InputStreamReader( is ));
	    BufferedReader reader = null;
	    String response = null;
	    
//		char current_dir[MAXPATHLEN];
//		char parent_dir[MAXPATHLEN];
//		char virtual_dir[MAXPATHLEN];
//		char reply[SENDBUFSIZE];
//		char data_buff[DATABUFSIZE];
//		char read_buff[RCVBUFSIZE];
//		char *str;
		boolean is_loged = false;
		boolean state_user = false;
//		char rename_from[MAXPATHLEN];
		int client_port = 8082;

		ServerSocket passiveClient = null;//new ServerSocket( port );

        Socket clientSocket = null;
        BufferedWriter clientWriter = null;

        File dir = null;
        String rename_from = "";
        
		public FTPSession( Socket s, int c_port, String rootdir )
		{
			dir = new File(rootdir);
			mySocket = s;
			client_port = c_port;
			Thread t = new Thread( this );
			t.setDaemon( true );
			t.start();
		}

		public void run()
		{
			try
			{
				InputStream is = mySocket.getInputStream();
				OutputStream os = mySocket.getOutputStream();
				if ( is == null) return;
				reader = new BufferedReader( new InputStreamReader( is ));
				writer = new BufferedWriter( new OutputStreamWriter( os ));

				interract();
				
				reader.close();
				writer.close();
			}
			catch ( Exception ioe )
			{
				try
				{
				}
				catch ( Throwable t ) {}
			}
//			catch ( InterruptedException ie )
//			{
//				// Thrown by sendError, ignore and exit the thread.
//			}
//			default(Exception e)
//			{
//				
//			}
		}

	    /**
	     * Sends a raw command to the FTP server.
	     */
	    private void sendLine(String line)
	    {
	    	try
	    	{
		        if (mySocket == null) {
		            throw new IOException("SimpleFTP is not connected.");
		        }
		        try {
		            writer.write(line + "\r\n");
		            writer.flush();
		            if (DEBUG) {
		                System.out.println("> " + line);
		            }
		        }
		        catch (IOException e) {
		        	mySocket = null;
		            throw e;
		        }
	    	}
	    	catch(Exception e)
	    	{
	    		System.out.println(e.toString());
	    	}
	    }

	    private String readLine() 
	    {
	    	try
	    	{
		        String line = reader.readLine();
		        if (DEBUG) {
		            System.out.println("< " + line);
		        }
		        return line;
	    	}
	    	catch(Exception e)
	    	{
	    		System.out.println(e.toString());
	    	}
	    	return null;
	    }

	    /**
	     * Get the next command from the client socket.
	     */
	    CMDS get_command() 
	    {
	        response = readLine();

//	        char read_buff[RCVBUFSIZE];
//	    	memset((char *)&read_buff, 0, RCVBUFSIZE);
//	    	read_buff[0]='\0';
//	    	char *rcv=read_buff;
//	    	int cmd_status = -1;
//	    	int recvbuff = recv(conn_fd,read_buff,RCVBUFSIZE,0);
//	    	if(recvbuff<1) {
//	    		return CMD_CLOSE;
//	    	}
//	    	if(recvbuff==RCVBUFSIZE) {
//	    		return CMD_UNKNOWN;
//	    	}
	    	// printf("Received:%s\n",rcv);
        	if(response==null)
        		return CMDS.CMD_EMPTY;

        	//To avoid Warning "not used"
        	//parse_input(NULL,NULL); 
        	
        	int len = response.length();
        	if(len<3)
        		return CMDS.CMD_UNKNOWN;
        	switch(response.charAt(0)) 
        	{
        		case 'A':
        			switch(response.charAt(1)) {
        				case 'B':
        					if(response.startsWith("ABOR")) 
        						return CMDS.CMD_ABOR;
        				case 'C':
        					if(response.startsWith("ACCT")) 
        						return CMDS.CMD_ACCT;
        				case 'L':
        					if(response.startsWith("ALLO")) 
        						return CMDS.CMD_ALLO;
        				case 'P':
        					if(response.startsWith("APPE")) 
        						return CMDS.CMD_APPE;
        				return CMDS.CMD_UNKNOWN;
        			}
        		case 'C':
        			if(response.startsWith("CWD")) 
        				return CMDS.CMD_CWD;
        			else if(response.startsWith("CDUP"))
        				return CMDS.CMD_CDUP;
        			return CMDS.CMD_UNKNOWN;

        		case 'D':
        			if(response.startsWith("DELE")) 
        				return CMDS.CMD_DELE;
        			return CMDS.CMD_UNKNOWN;
        		case 'E':
        			if(response.startsWith("EPSV")) 
        				return CMDS.CMD_EPSV;
        			return CMDS.CMD_UNKNOWN;
        		case 'H':
        			if(response.startsWith("HELP")) 
        				return CMDS.CMD_HELP;
        			return CMDS.CMD_UNKNOWN;
        		case 'L':
        			if(response.startsWith("LIST")) 
        				return CMDS.CMD_LIST;
        			return CMDS.CMD_UNKNOWN;
        		case 'M':
        			switch(response.charAt(1)) {
        				case 'K':
    	        			if(response.startsWith("MKD")) 
        						return CMDS.CMD_MKD;
        				case 'O':
    	        			if(response.startsWith("MODE")) 
        						return CMDS.CMD_MODE;
        				return CMDS.CMD_UNKNOWN;
        			}
        		case 'N':
        			switch(response.charAt(1)) {
        				case 'L':
    	        			if(response.startsWith("NLST")) 
        						return CMDS.CMD_NLST;
        				case 'O':
    	        			if(response.startsWith("NOOP")) 
        						return CMDS.CMD_NOOP;
        				return CMDS.CMD_UNKNOWN;
        			}
        		case 'P':
        			switch(response.charAt(1)) {
        				case 'A':
    	        			if(response.startsWith("PASS")) 
    							return CMDS.CMD_PASS;
    	        			if(response.startsWith("PASV")) 
    							return CMDS.CMD_PASV;
    						return CMDS.CMD_UNKNOWN;
        				case 'O':
    	        			if(response.startsWith("PORT")) 
    							return CMDS.CMD_PORT;
        				case 'W':
    	        			if(response.startsWith("PWD")) 
        						return CMDS.CMD_PWD;
        				return CMDS.CMD_UNKNOWN;
        			}
        		case 'Q':
        			if(response.startsWith("QUIT")) 
    					return CMDS.CMD_QUIT;
    				return CMDS.CMD_UNKNOWN;
        		case 'R':
        			switch(response.charAt(1)) {
        				case 'E':
        					switch(response.charAt(2)) {
        						case 'I':
        		        			if(response.startsWith("REIN")) 
        								return CMDS.CMD_REIN;
        						case 'S':
        		        			if(response.startsWith("REST")) 
        								return CMDS.CMD_REST;
        						case 'T':
        		        			if(response.startsWith("RETR")) 
        								return CMDS.CMD_RETR;
        						return CMDS.CMD_UNKNOWN;
        					}
        					return CMDS.CMD_UNKNOWN;
        				case 'M':
		        			if(response.startsWith("RMD")) 
        						return CMDS.CMD_RMD;
        				case 'N':
        					switch(response.charAt(2)) {
        						case 'F':
        		        			if(response.startsWith("RNFR")) 
        								return CMDS.CMD_RNFR;
        						case 'T':
        		        			if(response.startsWith("RNTO")) 
        								return CMDS.CMD_RNTO;
        						return CMDS.CMD_UNKNOWN;
        					}
        				return CMDS.CMD_UNKNOWN;
        			}
        		case 'S':
        			switch(response.charAt(1)) {
        				case 'I':
		        			if(response.startsWith("SITE")) 
        						return CMDS.CMD_SITE;
        				case 'M':
		        			if(response.startsWith("SMNT")) 
        						return CMDS.CMD_SMNT;
        				case 'T':
        					switch(response.charAt(2)) {
        						case 'A':
        		        			if(response.startsWith("STAT")) 
        								return CMDS.CMD_STAT;
        						case 'O':
        		        			if(response.startsWith("STOR")) 
        								return CMDS.CMD_STOR;
        		        			if(response.startsWith("STOU")) 
        								return CMDS.CMD_STOU;
        						case 'R':
        		        			if(response.startsWith("STRU")) 
        								return CMDS.CMD_STRU;
        					}
        					return CMDS.CMD_UNKNOWN;
        				case 'Y':
		        			if(response.startsWith("SYST")) 
        						return CMDS.CMD_SYST;
        					return CMDS.CMD_UNKNOWN;
        			}
        		case 'T':
        			if(response.startsWith("TYPE")) 
        				return CMDS.CMD_TYPE;
        			return CMDS.CMD_UNKNOWN;
        		case 'U':
        			if(response.startsWith("USER")) 
        				return CMDS.CMD_USER;
        			return CMDS.CMD_UNKNOWN;
        		default:
        			return CMDS.CMD_UNKNOWN;
        		}
    	}

	    void stat_file(File f,BufferedWriter clientWriter) 
	    {
	    	if(f.isDirectory())
	    	{
	    		write_list(f, clientWriter);
	    	}
	    	else
	    	{
	    		String line = "";
				line += "rw-rw-rw-";
				line += " "+"001";
				line += " "+"user";
				line += " "+"grup";
				line += " "+Long.toString(f.length(),8);
				line += " "+"Sep 09 11:30";
				line += " "+f.getName()+"\r\n";
	    		
    	        try {
    	            clientWriter.write(line + "\r\n");
    	            if (DEBUG) 
    	            {
    	                System.out.println("> " + line);
    	            }
    	        }
    	        catch (Exception e) 
    	        {
					sendLine(REPL_451);
    	        }
	    	}
    		sendLine(REPL_211_END);

//    		char line[300];
//	    	struct stat s_buff;
//	    	int status = stat(file_path,&s_buff);
//	    	if(status==0) {
//	    		reply[0]='\0';
//	    		int len = sprintf(reply,REPL_211_STATUS,file_path);
//	    		send_repl_len(sock,reply,len);
//	    		int b_mask = s_buff.st_mode & S_IFMT;
//	    		if(b_mask == S_IFDIR) {
//	    			if(getcwd(line,300)!=NULL) {	
//	    				int status = chdir(file_path);
//	    				if(status != 0) {
//	    					send_repl(sock,REPL_450);
//	    					//free(line);
//	    					return FALSE;
//	    				}
//	    				else {
//	    					if(!write_list(sock, sock, file_path)) {
//	    						send_repl(sock,REPL_450);
//	    						return FALSE;
//	    					}
//	    					int status = chdir(line);
//	    					if(status!=0) {
//	    						send_repl(sock,REPL_450);
//	    						//free(line);
//	    						return FALSE;
//	    					}
//	    				}
//	    			} else {
//	    				send_repl(sock,REPL_450);
//	    				//free(line);
//	    				return FALSE;
//	    					
//	    			}
//	    		} else if(b_mask == S_IFREG){
//	    			if(get_file_info_stat(file_path,line,&s_buff)) {
//	    				if(send_repl_client(sock,line)) {
//	    					send_repl(sock,REPL_450);
//	    					//free(line);
//	    					return FALSE;
//	    				}
//	    			}
//	    		} 
//	    		send_repl(sock,REPL_211_END);
//	    	}
//	    	else {
//	    		send_repl(sock,REPL_450);
//	    		return FALSE;
//	    	}
//	    	free(line);
//	    	return TRUE;
	    }

	    void write_list(File dir,BufferedWriter clientWriter)
	    {
	    	File[] children = dir.listFiles();
	    	for(int fi=0;fi<children.length;fi++) 
	    	{
	    		String line = "";
//	    		struct stat s_buff;
//	    		if(is_special_dir(file_name))
//	    			do_nothing();//return FALSE;
//	    		int status = stat(file_name,&s_buff);
//	    		if(status==0) {
//	    			return get_file_info_stat(file_name,line,&s_buff);
//	    		}
	    		/**
	    		 * Write file statics in a line using the buffer from stat(...) primitive
	    		 */
//    			char date[16];
//    			char mode[11]	= "----------";
//    			line[0]='\0';
//    			struct passwd * pass_info = getpwuid(s_buff->st_uid);
//    			if(pass_info!=NULL) {
//    				struct group * group_info = getgrgid(s_buff->st_gid);
//    				if(group_info!=NULL) {
//    					int b_mask = s_buff->st_mode & S_IFMT;
//    					if(b_mask == S_IFDIR) {
//    						mode[0]='d';
//    					} else if(b_mask == S_IFREG){
//    						mode[0]='-';
//    					} else {
//    						return FALSE;
//    					}
//    					mode[1] = (s_buff->st_mode & S_IRUSR)?'r':'-';
//    					mode[2] = (s_buff->st_mode & S_IWUSR)?'w':'-';
//    					mode[3] = (s_buff->st_mode & S_IXUSR)?'x':'-';
//    					mode[4] = (s_buff->st_mode & S_IRGRP)?'r':'-';
//    					mode[5] = (s_buff->st_mode & S_IWGRP)?'w':'-';
//    					mode[6] = (s_buff->st_mode & S_IXGRP)?'x':'-';
//    					mode[7] = (s_buff->st_mode & S_IROTH)?'r':'-';
//    					mode[8] = (s_buff->st_mode & S_IWOTH)?'w':'-';
//    					mode[9] = (s_buff->st_mode & S_IXOTH)?'x':'-';
//    					strftime(date,13,"%b %d %H:%M",localtime(&(s_buff->st_mtime)));

//    					write_file(
//    						line,mode,s_buff->st_nlink,
//    						pass_info->pw_name,
//    						group_info->gr_name,
//    						s_buff->st_size,date,
//    						file_name
//    					);
    					/**
    					 * Writes the file statics in a formated string, given a pointer to the string.
    					 */
//    					static int write_file(char *line,const char *mode,int num,const char *user,const char * group,int size,const char *date,const char*fl_name) {
    						//sprintf(line,"%s %3d %-4s %-4s %8d %12s %s\r\n",mode,num,user,group,size,date,fl_name);
						if(children[fi].isDirectory())
						{
		    				line += "drw-rw-rw-";
							line += " "+"1";
							line += " "+"user";
							line += " "+"grup";
							line += " "+Long.toString(children[fi].length(),8);
							line += " "+"Sep 9 1980";
							line += " "+children[fi].getName()+"\r\n";
						}
						else
						{
		    				line += "-rw-rw-rw-";
							line += " "+"1";
							line += " "+"user";
							line += " "+"grup";
							line += " "+Long.toString(children[fi].length(),8);
							line += " "+"Sep 9 1980";
							line += " "+children[fi].getName()+"\r\n";
						}
//    					return TRUE;
//    				}
//    			}
//    			return FALSE;
//	    		if(line.length()>0) 
	    		{
	    	        try {
	    	            clientWriter.write(line + "\r\n");
	    	            if (DEBUG) 
	    	            {
	    	                System.out.println("> " + line);
	    	            }
	    	        }
	    	        catch (Exception e) 
	    	        {
    					sendLine(REPL_451);
	    	        }
	    		}
//	    		else
//	    		{
//					sendLine(REPL_451);
//	    		}
	    	}
	    	
	    	try
	    	{
	    		clientWriter.flush();
	    	}
	    	catch(Exception e)
	    	{
	    		System.out.println(e.toString());
	    	}
	    }
	    
		/**
		 * Main cycle for client<->server communication. 
		 * This is done synchronously. On each client message, it is parsed and recognized,
		 * certain action is performed. After that we wait for the next client message
		 * 
		 */
		void interract()//(int conn_fd,cmd_opts *opts) 
		{
			//static int BANNER_LEN = strlen(REPL_220);
			//int userid = opts->userid;
//			int client_fd=-1;
//			int len;
//			int _type ;
			int type = 2; // ASCII TYPE by default
			sendLine(REPL_220);

			boolean running = true;
			while(running) 
			{
				CMDS result = get_command();
				if(result != CMDS.CMD_RNFR && result != CMDS.CMD_RNTO && result != CMDS.CMD_NOOP)
				{
					//rename_from[0]='\0';
				}
				switch(result) {
					case CMD_UNKNOWN:
						sendLine(REPL_500);
						break;
					case CMD_EMPTY:
					case CMD_CLOSE:
						//close_conn(conn_fd);
						running = false;
						break;
					case CMD_USER:
						if(response.length()==4 || response.equalsIgnoreCase("USER anonymous")) 
						{
							state_user = true;
							sendLine(REPL_331_ANON);
						}
						else 
						{
							state_user = true;
							sendLine(REPL_331_ANON);
//							sendLine(REPL_332);
						}
						break;
					case CMD_PASS:
						if(!state_user) 
						{
							sendLine(REPL_503);
						}
						else 
						{
							is_loged = true;
							state_user = false;
							sendLine(REPL_230);
						}
						break;
					case CMD_PORT:
						if(!is_loged) 
							sendLine(REPL_530);
						else {
							System.out.println("Need to parse "+response);
							//client_port = parse_port_data(data_buff,client_addr);
							if(client_port<0) 
							{
								sendLine(REPL_501);
								client_port = 0;
							} 
							else 
							{
								sendLine(REPL_200);
							}
						}
						break;
					case CMD_PASV:
						if(!is_loged) sendLine(REPL_530);
						else 
						{
							sendLine(REPL_502);
						}
						break;
					case CMD_EPSV:
						if(!is_loged) sendLine(REPL_530);
						else 
						{
							try
							{
								if(passiveClient==null)
									passiveClient = new ServerSocket( client_port );								
								sendLine(REPL_229.replace("%s", ""+client_port));
							}
							catch(Exception e)
							{
								sendLine(REPL_502);								
							}
						}
						break;
					case CMD_SYST:
						sendLine(REPL_215.replace("%s", "UNIX"));
						break;
					case CMD_LIST:
						if(!is_loged) sendLine(REPL_530);
						else 
						{
							try
							{
								if(passiveClient!=null)
								{
									clientSocket = passiveClient.accept();

									sendLine(REPL_150);
								}
								else
								{
							        clientSocket = new Socket(mySocket.getInetAddress(), client_port);
								}
						        clientWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
	
								write_list(dir,clientWriter);
								clientWriter.close();
								clientSocket.close();
								sendLine(REPL_226);
							}
							catch(Exception e)
							{
								System.out.println(e.toString());
							}
						}
						break;
					case CMD_RETR:
						if(!is_loged) sendLine(REPL_530);
						else 
						{
							if(response.length()==4) 
							{
								sendLine(REPL_501);
							} 
							else 
							{
								try
								{
									if(passiveClient!=null)
										clientSocket = passiveClient.accept();
									else
										clientSocket = new Socket(mySocket.getInetAddress(), client_port);
								}
								catch(Exception e)
								{
									System.out.println(e.toString());
								}

								sendLine(REPL_150);

								File f = new File(dir,response.substring(5));
						          
						        FileChannel in = null;
						        OutputStream out = null;

						        boolean success = true;
						        try {
						            in = new FileInputStream(f).getChannel();
						            out = clientSocket.getOutputStream();
						            WritableByteChannel channel = Channels.newChannel(out);
						            
						            ByteBuffer buffer = ByteBuffer.allocateDirect(64*1024);
						            while (in.read(buffer) != -1) {
						                buffer.flip();

						                while(buffer.hasRemaining()){
								            channel.write(buffer);
						                    //out.write(buffer.array());
						                }

						                buffer.clear();
						            }
						            in.close();
						            out.close();
						        } catch (IOException e) {
						        	success = false;
						            e.printStackTrace();
						        } 
						        catch (Exception e)
						        {
						        	System.out.println(e.toString());
						        	try
						        	{
							            in.close();
							            out.close();
									}
									catch(Exception ge)
									{
										System.out.println(ge.toString());
									}
							            
						        }

//								clientWriter.close();
						        try
						        {
						        	clientSocket.close();
								}
								catch(Exception e)
								{
									System.out.println(e.toString());
								}

								sendLine(REPL_226);
							}
						}
						break;
					case CMD_STOU:
						if(!is_loged) sendLine(REPL_530);
						else 
						{
							try
							{
					        clientSocket = new Socket(mySocket.getInetAddress(), client_port);

							InputStream is = clientSocket.getInputStream();
//
//							File f = new File(dir,response.substring(5));
							File f = new File(dir,"XXXXX");
							FileOutputStream os = new FileOutputStream(f);
							
					        byte[] buffer = new byte[64*1024];
					        int bytesRead = 0;
					        while ((bytesRead = is.read(buffer)) != -1) 
					        {
					            os.write(buffer, 0, bytesRead);
					        }
					        os.flush();
					        os.close();
							}
							catch(Exception e)
							{
								System.out.println(e.toString());
							}

							sendLine(REPL_226);
						}
						break;
					case CMD_STOR:
						if(!is_loged) sendLine(REPL_530);
						else {
							if(response.length()==4) 
								sendLine(REPL_501);
							else {
								try
								{
									if(passiveClient!=null)
									{
										clientSocket = passiveClient.accept();

										sendLine(REPL_150);
									}
									else
									{
										clientSocket = new Socket(mySocket.getInetAddress(), client_port);
									}

									InputStream is = clientSocket.getInputStream();

								File f = new File(dir,response.substring(5));
								FileOutputStream os = new FileOutputStream(f);
								
						        byte[] buffer = new byte[64*1024];
						        int bytesRead = 0;
						        while ((bytesRead = is.read(buffer)) != -1) 
						        {
						            os.write(buffer, 0, bytesRead);
						        }
						        os.flush();
						        os.close();
								}
								catch(Exception e)
								{
									System.out.println(e.toString());
								}

								sendLine(REPL_226);
							}
						}
						break;
					case CMD_SITE:
						if(!is_loged) sendLine(REPL_530);
						else {
							if(response.length()==4) 
								sendLine(REPL_501);
							else 
							{
								sendLine(REPL_202);
							}
						}
						break;
					case CMD_PWD:
						if(!is_loged) sendLine(REPL_530);
						else {
							sendLine(REPL_257_PWD.replace("%s", dir.getAbsolutePath()));
						}
						break;
					case CMD_CDUP:
						if(!is_loged) sendLine(REPL_530);
						else 
						{
							File ndir = dir.getParentFile();
							if(ndir!=null)
							{								
								dir = ndir;
								SimpleFTPServer.lastDir = dir.getAbsolutePath();
								sendLine(REPL_250);
							}
							else
							{
								sendLine(REPL_550);
							}
						}
						break;
					case CMD_CWD:
						if(!is_loged) sendLine(REPL_530);
						else {
							if(response.length()==4) 
								sendLine(REPL_501);
							else 
							{
								File ndir = null;
								
								String path = response.substring(4);
								if(dir==null||path.startsWith("/"))
								{
									ndir = new File(path);									
								}
								else
								{
									ndir = new File(dir,path);
								}
								
								if(ndir.exists()&&ndir.isDirectory())
								{								
									dir = ndir;
									SimpleFTPServer.lastDir = dir.getAbsolutePath();
									sendLine(REPL_250);
								}
								else
								{
									sendLine(REPL_550);
								}
							}
						}
						break;
					case CMD_QUIT:
						sendLine(REPL_221);
//						if(client_fd!=-1){
//							close_conn(client_fd);
//						}
//						close_conn(conn_fd);
						running = false;
						break;
					case CMD_TYPE:
						
//						switch(type[0]) {
//						case 'I':
//							return 1;
//						case 'A':
//							return 2;
//						case 'L':
//							if(len<3)
//								return -1;
//							if(type[2]=='7')
//								return 3;
//							if(type[2]=='8')
//								return 4;
//					}

//						_type = get_type(data_buff);
						String stype = response.substring(5);
						int ntype = -1;
						if(stype.equalsIgnoreCase("I"))
							ntype = 1;
						else
						if(stype.equalsIgnoreCase("A"))
							ntype = 2;
						else
						if(stype.equalsIgnoreCase("L7"))
							ntype = 3;
						else
						if(stype.equalsIgnoreCase("L8"))
							ntype = 4;
						
						if(ntype==-1) 
						{
							sendLine(REPL_500);
						}
						else 
						{
							type=ntype;
							sendLine(REPL_200);
						}
						break;
					case CMD_STAT:
						if(!is_loged) sendLine(REPL_530);
						else {
							if(response.length()==4) 
								sendLine(REPL_501);
							else 
							{
								File f = new File(dir,response.substring(5));

								try
								{
									clientSocket = new Socket(mySocket.getInetAddress(), client_port);
							        clientWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
	
									stat_file(f,clientWriter);
									//write_list(clientWriter);
									clientWriter.close();
									clientSocket.close();
								}
								catch(Exception e)
								{
									System.out.println(e.toString());
								}
							}
						}
						break;
					case CMD_ABOR:
						if(!is_loged) sendLine(REPL_530);
						else 
						{
//							if(client_fd!=-1){
//								close_connection(client_fd);
//							} 
							sendLine(REPL_226);
						}
						break;
					case CMD_MKD:
						if(!is_loged) sendLine(REPL_530);
						else 
						{
							if(response.length()==4) 
								sendLine(REPL_501);
							else 
							{
								File ndir = new File(dir,response.substring(4));
								ndir.mkdir();
								sendLine(REPL_257.replace("%s", ndir.getName()));
								//make_dir(conn_fd,data_buff,reply);
							}
						}
						break;
					case CMD_RMD:
						if(!is_loged) sendLine(REPL_530);
						else {
							if(response.length()==4) 
								sendLine(REPL_501);
							else 
							{
								File rdir = new File(dir,response.substring(4));
								if((!rdir.exists())||(!rdir.isDirectory()))
								{
									sendLine(REPL_550);									
								}
								else
								{
									//FileSystem.deleteDirectory(rdir);
									sendLine(REPL_250);
								}
							}
						}
						break;
					case CMD_DELE:
						if(!is_loged) sendLine(REPL_530);
						else {
							if(response.length()==4) 
								sendLine(REPL_501);
							else 
							{
								File rdir = new File(dir,response.substring(4));
								if((!rdir.exists())||(rdir.isDirectory()))
								{
									sendLine(REPL_550);																		
								}
								else
								{
									//FileSystem.deleteFile(rdir);
									sendLine(REPL_250);
								}
							}
						}
						break;
					case CMD_RNFR:
						if(!is_loged) sendLine(REPL_530);
						else {
							if(response.length()==4) 
								sendLine(REPL_501);
							else 
							{
								rename_from = response.substring(5);
								sendLine(REPL_350);
							}
						}
						break;
					case CMD_RNTO:
						if(!is_loged) sendLine(REPL_530);
						else {
							if(response.length()==4) 
								sendLine(REPL_501);
							else {
								if(rename_from==null || rename_from.length()==0) 
								{
									sendLine(REPL_501);
								} 
								else 
								{
									File rnm = new File(dir,rename_from);
									if(rnm.exists())
									{
										rnm.renameTo(new File(dir,response.substring(5)));
										sendLine(REPL_250);
									}
									else
									{
										sendLine(REPL_550);										
									}
								}
							}
						}
						break;
					case CMD_NOOP:
						sendLine(REPL_200);
						break;
					case CMD_STRU:
						if(!is_loged) sendLine(REPL_530);
						else {
							if(response.length()==4) 
								sendLine(REPL_501);
							else {
								switch(response.charAt(6)) {
									case 'F':
										sendLine(REPL_200);
										break;
									case 'P':
									case 'R':
										sendLine(REPL_504);
										break;
									default:
										sendLine(REPL_501);
									
								}
							}
						}
						break;
					case CMD_HELP:
					//	if(data_buff==NULL || strlen(data_buff)==0 || data_buff[0]=='\0') {
							sendLine(REPL_214);
							sendLine("    Some help message.\r\n    Probably nobody needs help from telnet.\r\n    See rfc959.\r\n");
							sendLine(REPL_214_END);
					//	}
						break;
					case CMD_MODE:
						if(!is_loged) sendLine(REPL_530);
						else {
							if(response.length()==4) 
								sendLine(REPL_501);
							else {
								switch(response.charAt(6)) 
								{
									case 'S':
										sendLine(REPL_200);
										break;
									case 'B':
									case 'C':
										sendLine(REPL_504);
										break;
									default:
										sendLine(REPL_501);									
								}
							}
						}
						break;
					default:
						sendLine(REPL_502);
				}
			}			
		}
	}
}
