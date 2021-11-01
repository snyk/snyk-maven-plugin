import io.snyk.snyk_maven_plugin.it.ITUtils

ITUtils.createDummyAtDownloadDestination(basedir, 0);
ITUtils.deleteSha256For(basedir);

return true;
