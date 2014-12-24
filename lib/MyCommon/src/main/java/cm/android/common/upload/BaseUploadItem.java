package cm.android.common.upload;

public class BaseUploadItem implements IUploadInterface {

    private int operate;

    @Override
    public void start() {
        operate = OPERATE_START;
    }

    @Override
    public void stop() {
        operate = OPERATE_STOP;
    }

    @Override
    public void delete() {
        operate = OPERATE_DELETE;
    }

    @Override
    public boolean isStopped() {
        return operate == OPERATE_STOP;
    }

    @Override
    public boolean isDeleted() {
        return operate == OPERATE_DELETE;
    }

    @Override
    public boolean isWaiting() {
        return operate == OPERATE_START;
    }

}
