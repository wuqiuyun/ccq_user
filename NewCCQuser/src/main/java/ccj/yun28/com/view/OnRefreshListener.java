package ccj.yun28.com.view;

public interface OnRefreshListener {

	/**
	 * ����ˢ��ִ�е�ˢ������, ʹ��ʱ, ��ˢ�����֮��, ��Ҫ�ֶ��ĵ���onRefreshFinish(),
	 * ȥ����ͷ����
	 */
	public void onRefresh();

	/**
	 * �����ظ���ʱ�ص� �����ظ������֮��, ��Ҫ�ֶ��ĵ���onRefreshFinish(), ȥ���ؽŲ���
	 */
	public void onLoadMoring();
}
