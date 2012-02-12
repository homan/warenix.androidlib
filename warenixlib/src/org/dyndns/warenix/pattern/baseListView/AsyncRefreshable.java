package org.dyndns.warenix.pattern.baseListView;

public interface AsyncRefreshable {
	public void asyncRefresh();

	public void cancelAsyncRefresh();
}
