package com.legacy_lens.config;

//import jakarta.annotation.PreDestroy;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.context.event.EventListener;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//public class OllamaLifecycleManager {
//
//	@EventListener(ApplicationReadyEvent.class)
//	public void startOllama() {
//		log.info("Starting Ollama server...");
//		try {
//			new ProcessBuilder("ollama", "serve").redirectOutput(ProcessBuilder.Redirect.DISCARD)
//					.redirectError(ProcessBuilder.Redirect.DISCARD).start();
//			log.info("Ollama server started successfully");
//		} catch (Exception e) {
//			log.error("Failed to start Ollama: {}", e.getMessage());
//		}
//	}
//
//	@PreDestroy
//	public void stopOllama() {
//		log.info("Stopping Ollama server...");
//		String os = System.getProperty("os.name").toLowerCase();
//		try {
//			if (os.contains("win")) {
//				killWindows();
//			} else {
//				killUnix();
//			}
//			log.info("Ollama server stopped");
//		} catch (Exception e) {
//			log.error("Failed to stop Ollama: {}", e.getMessage());
//		}
//	}
//
//	private void killWindows() throws Exception {
//		for (String target : new String[] { "ollama.exe", "ollama app.exe" }) {
//			new ProcessBuilder("cmd", "/c", "taskkill /F /IM \"" + target + "\"").redirectErrorStream(true).start()
//					.waitFor();
//			log.info("Killed process: {}", target);
//		}
//	}
//
//	private void killUnix() throws Exception {
//		new ProcessBuilder("pkill", "-f", "ollama").redirectErrorStream(true).start().waitFor();
//		log.info("Killed ollama (unix)");
//	}
//}

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class OllamaLifecycleManager {

    private static final Logger log = LoggerFactory.getLogger(OllamaLifecycleManager.class);

    private static String currentUser() {
        return SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getName()
                : "anonymous";
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startOllama() {
        String cu = currentUser();
        log.debug("Entering startOllama | params: none");
        log.info("User={} | action=startOllama | entity=Ollama | id=n/a", cu);
        log.info("Processing request | entity=Ollama | phase=startup");
        String os = System.getProperty("os.name").toLowerCase();
        log.info("Branch taken | method=startOllama | condition=os | value={}", os);
        try {
            ProcessBuilder pb;
            if (os.contains("win")) {
                pb = new ProcessBuilder("ollama", "serve");
            } else {
                pb = new ProcessBuilder("ollama", "serve");
            }
            pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
            pb.redirectError(ProcessBuilder.Redirect.DISCARD);
            pb.start();
            log.info("Status updated | entity=Ollama | id=n/a | newStatus=started");
            log.debug("Exiting startOllama | completed successfully");
        } catch (Exception e) {
            log.error("Exception in startOllama | user={} | message={}", cu, e.getMessage(), e);
            log.debug("Exiting startOllama | completed with error");
        }
    }

    @PreDestroy
    public void stopOllama() {
        String cu = currentUser();
        log.debug("Entering stopOllama | params: none");
        log.info("User={} | action=stopOllama | entity=Ollama | id=n/a", cu);
        log.info("Processing request | entity=Ollama | phase=shutdown");
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            log.info("Branch taken | method=stopOllama | condition=os | value=windows");
            killWindows();
        } else {
            log.info("Branch taken | method=stopOllama | condition=os | value=unix");
            killUnix();
        }
        log.debug("Exiting stopOllama | completed successfully");
    }

    private void killWindows() {
        String[] targets = {"ollama.exe", "ollama app.exe"};
        for (String target : targets) {
            try {
                ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "taskkill /F /IM \"" + target + "\"");
                pb.redirectErrorStream(true);
                Process process = pb.start();
                int exitCode = process.waitFor();
                log.info("taskkill '{}' exited with code {}", target, exitCode);
            } catch (Exception e) {
                log.error("Exception in killWindows | user=anonymous | message={}", e.getMessage(), e);
            }
        }
    }

    private void killUnix() {
        try {
            ProcessBuilder pb = new ProcessBuilder("pkill", "-f", "ollama");
            pb.redirectErrorStream(true);
            Process process = pb.start();
            int exitCode = process.waitFor();
            log.info("pkill ollama exited with code {}", exitCode);
        } catch (Exception e) {
            log.error("Exception in killUnix | user=anonymous | message={}", e.getMessage(), e);
        }
    }
}
