import org.codehaus.plexus.util.FileUtils;

String log = FileUtils.fileRead(new File(basedir, "build.log"))

if (!log.contains("Medium severity vulnerability found in tiff/libtiff5")) {
    throw new Exception("Expected medium vulnerability not found")
}

if (!log.contains("Critical severity vulnerability found in zlib/zlib1g")) {
    throw new Exception("Expected critical vulnerability not found")
}

return true
