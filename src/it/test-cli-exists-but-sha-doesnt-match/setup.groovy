import io.snyk.snyk_maven_plugin.it.ITUtils

ITUtils.createDummyAtDownloadDestination(basedir, 0);
ITUtils.createInvalidSha256For(basedir);

return true;
