package us.codecraft.blackhole.monitor;

import java.io.IOException;
import java.net.UnknownHostException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import us.codecraft.blackhole.Configure;
import us.codecraft.blackhole.SpringLocator;

/**
 * 
 * @author yihua.huang@dianping.com
 * @date Dec 14, 2012
 */
@Component
public class BlackHole {

	private boolean isShutDown = false;

	private static Logger logger = Logger.getLogger(BlackHole.class);

	private TCPSocketMonitor tcpSocketMonitor;

	private UDPSocketMonitor udpSocketMonitor;

	public void start() throws UnknownHostException, IOException {
		tcpSocketMonitor = SpringLocator.getBean(TCPSocketMonitor.class);
		tcpSocketMonitor.start();
		udpSocketMonitor = SpringLocator.getBean(UDPSocketMonitor.class);
		udpSocketMonitor.start();
	}

	private static void parseArgs(String[] args) throws ParseException {
		Options options = new Options();
		options.addOption(new Option("f", true, "input file"));
		options.addOption(new Option("c", true, "simple command"));
		CommandLineParser commandLineParser = new PosixParser();
		CommandLine commandLine = commandLineParser.parse(options, args);
		readOptions(commandLine);
	}

	private static void readOptions(CommandLine commandLine) {
		if (commandLine.hasOption("d")) {
			String filename = commandLine.getOptionValue("f");
			Configure.FILE_PATH = filename;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			parseArgs(args);
		} catch (ParseException e1) {
			logger.warn("parse args error");
		}
		SpringLocator.applicationContext = new ClassPathXmlApplicationContext(
				"spring/applicationContext*.xml");
		BlackHole blackHole = SpringLocator.getBean(BlackHole.class);
		try {
			blackHole.start();
		} catch (UnknownHostException e) {
			logger.warn("init failed ", e);
		} catch (IOException e) {
			logger.warn("init failed ", e);
		}
		while (!blackHole.isShutDown) {
			try {
				Thread.sleep(10000000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
