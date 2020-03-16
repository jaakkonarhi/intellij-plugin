package fi.aalto.cs.apluscourses.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public abstract class Module extends Component {

  @NotNull
  private final URL url;

  /**
   * Constructs a module with the given name and URL.
   * @param name The name of the module.
   * @param url The URL from which the module can be downloaded.
   */
  public Module(@NotNull String name, @NotNull URL url) {
    super(name);
    this.url = url;
  }

  /**
   * Returns a module constructed from the given JSON object. The object should contain the name of
   * the module and the URL from which the module can be downloaded. Additional members in the
   * object don't cause any errors. Example of a valid JSON object:
   * <pre>
   * {
   *   "name": "My Module",
   *   "url": "https://example.com"
   * }
   * </pre>
   * @param jsonObject The JSON object containing information about a single module.
   * @param factory    A {@link ModelFactory} object that is responsible for actual object creation.
   * @return A module constructed from the given JSON object.
   * @throws MalformedURLException  If the URL of the module is malformed.
   * @throws org.json.JSONException If the jsonObject doesn't contain "name" and "url" keys with
   *                                string values.
   */
  @NotNull
  public static Module fromJsonObject(@NotNull JSONObject jsonObject, @NotNull ModelFactory factory)
      throws MalformedURLException {
    String name = jsonObject.getString("name");
    URL url = new URL(jsonObject.getString("url"));
    return factory.createModule(name, url);
  }

  @NotNull
  public URL getUrl() {
    return url;
  }

  @NotNull
  @Override
  public List<String> getDependencies() throws ModuleLoadException {
    return Stream.of(getLibraries(), getDependencyModules())
        .flatMap(List::stream)
        .collect(Collectors.toList());
  }

  public abstract List<String> getLibraries() throws ModuleLoadException;

  /**
   * Checks the state of the module (for an example by looking at the file system) and updates
   * #{@link Module#stateMonitor} to the correct state.
   */
  public abstract void updateState();

  public abstract List<String> getDependencyModules() throws ModuleLoadException;
}
