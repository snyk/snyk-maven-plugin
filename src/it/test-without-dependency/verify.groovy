// for known issues, no vulnerable paths found.
import java.io.*;
import org.codehaus.plexus.util.*;

String log = FileUtils.fileRead( new File( basedir, "build.log" ) );

if(!log.contains("for known issues, no vulnerable paths found.")) {
    throw new Exception("Missing dependency: axis:axis@1.4");
}

return true;
