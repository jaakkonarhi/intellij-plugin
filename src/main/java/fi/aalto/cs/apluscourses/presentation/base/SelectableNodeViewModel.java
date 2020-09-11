package fi.aalto.cs.apluscourses.presentation.base;

import fi.aalto.cs.apluscourses.presentation.filter.Filter;
import fi.aalto.cs.apluscourses.utils.Tree;
import fi.aalto.cs.apluscourses.utils.observable.ObservableProperty;
import fi.aalto.cs.apluscourses.utils.observable.ObservableReadWriteProperty;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SelectableNodeViewModel<T> extends BaseViewModel<T>
    implements Tree, Filterable {

  private static final SelectableNodeViewModel<?>[] EMPTY_ARRAY = new SelectableNodeViewModel<?>[0];

  @NotNull
  private final List<SelectableNodeViewModel<?>> children;
  public final ObservableProperty<Boolean> isVisible = new ObservableReadWriteProperty<>(true);

  private volatile boolean selected = false;

  public SelectableNodeViewModel(@NotNull T model,
                                 @Nullable List<SelectableNodeViewModel<?>> children) {
    super(model);
    this.children = Optional.ofNullable(children).orElse(Collections.emptyList());
  }

  /**
   * Applies a filter to this node, that is, sets the node visible if the filter applies to the node
   * or one of its descendants, or the filter is not applicable to this node.  Otherwise, sets the
   * node invisible.
   *
   * @param filter A filter.
   * @return True, if the filter applies to this node or one of its descendants, otherwise false.
   */
  public boolean applyFilter(Filter filter) {
    if (Thread.currentThread().isInterrupted()) {
      return true;
    }
    Optional<Boolean> visible = children.stream()
        .map(child -> child.applyFilter(filter))
        .reduce(Boolean::logicalOr) // we don't use anyMatch to avoid short-circuiting
        .filter(Boolean::booleanValue)
        .map(Optional::of)
        .orElseGet(() -> filter.apply(this));
    isVisible.set(visible.orElse(true));
    return visible.orElse(false);
  }

  public boolean isSelected() {
    return selected;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }

  @Override
  @NotNull
  public List<SelectableNodeViewModel<?>> getChildren() {
    return children;
  }

  @Override
  public void addVisibilityListener(Listener listener) {
    isVisible.addValueObserver(listener, Listener::visibilityChanged);
  }
}
