package cn.flyzy2005.fztutil.util;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;

import cn.flyzy2005.fztutil.bean.SinglePermission;
import cn.flyzy2005.fztutil.func.FuncCall;
import cn.flyzy2005.fztutil.func.FuncRational;
import cn.flyzy2005.fztutil.func.FuncResult;

/**
 * Created by Fly on 2017/5/7.
 */

public class PermissionHelper {
    public static PermissionObject with(AppCompatActivity activity) {
        return new PermissionObject(activity);
    }

    public static PermissionObject with(Activity activity) {
        return new PermissionObject(activity);
    }

    public static PermissionObject with(Fragment fragment) {
        return new PermissionObject(fragment);
    }

    public static class PermissionObject {

        private AppCompatActivity mAppCompatActivity;
        private Activity mActivity;
        private Fragment mFragment;

        PermissionObject(AppCompatActivity activity) {
            mAppCompatActivity = activity;
        }

        PermissionObject(Activity activity) {
            mActivity = activity;
        }

        PermissionObject(Fragment fragment) {
            mFragment = fragment;
        }

        public boolean has(String permissionName) {
            int permissionCheck;
            if (mAppCompatActivity != null) {
                permissionCheck = ContextCompat.checkSelfPermission(mAppCompatActivity, permissionName);
            } else if (mActivity != null) {
                permissionCheck = ContextCompat.checkSelfPermission(mActivity, permissionName);
            } else {
                permissionCheck = ContextCompat.checkSelfPermission(mFragment.getContext(), permissionName);
            }

            return permissionCheck == PackageManager.PERMISSION_GRANTED;
        }

        public PermissionRequestObject request(String permissionName) {
            if (mAppCompatActivity != null) {
                return new PermissionRequestObject(mAppCompatActivity, new String[]{permissionName});
            } else if (mActivity != null) {
                return new PermissionRequestObject(mActivity, new String[]{permissionName});
            } else {
                return new PermissionRequestObject(mFragment, new String[]{permissionName});
            }
        }

        public PermissionRequestObject request(String... permissionNames) {
            if (mAppCompatActivity != null) {
                return new PermissionRequestObject(mAppCompatActivity, permissionNames);
            } else if (mActivity != null) {
                return new PermissionRequestObject(mActivity, permissionNames);
            } else {
                return new PermissionRequestObject(mFragment, permissionNames);
            }
        }
    }

    public static class PermissionRequestObject {

        private static final String TAG = PermissionObject.class.getSimpleName();
        private AppCompatActivity mAppCompatActivity;
        private Activity mActivity;
        private FuncCall mDenyFunc;
        private Fragment mFragment;
        private FuncCall mGrantFunc;
        private String[] mPermissionNames;
        private ArrayList<SinglePermission> mPermissionsWeDontHave;
        private FuncRational mRationalFunc;
        private int mRequestCode;
        private FuncResult mResultFunc;

        PermissionRequestObject(AppCompatActivity activity, String[] permissionNames) {
            mAppCompatActivity = activity;
            mPermissionNames = permissionNames;
        }

        PermissionRequestObject(Fragment fragment, String[] permissionNames) {
            mFragment = fragment;
            mPermissionNames = permissionNames;
        }

        PermissionRequestObject(Activity activity, String[] permissionNames) {
            mActivity = activity;
            mPermissionNames = permissionNames;
        }

        /**
         * Execute the permission request with the given Request Code
         *
         * @param reqCode a unique request code in your activity
         */
        public PermissionRequestObject ask(int reqCode) {
            mRequestCode = reqCode;
            int length = mPermissionNames.length;
            mPermissionsWeDontHave = new ArrayList<>(length);
            for (String mPermissionName : mPermissionNames) {
                mPermissionsWeDontHave.add(new SinglePermission(mPermissionName));
            }

            if (needToAsk()) {
//                Log.i(TAG, "Asking for permission");
                if (mAppCompatActivity != null) {
                    ActivityCompat.requestPermissions(mAppCompatActivity, mPermissionNames, reqCode);
                } else if (mActivity != null) {
                    ActivityCompat.requestPermissions(mActivity, mPermissionNames, reqCode);
                } else {
                    mFragment.requestPermissions(mPermissionNames, reqCode);
                }
            } else {
//                Log.i(TAG, "No need to ask for permission");
                if(mResultFunc != null){
                    int arr[] = new int[length];
                    for(int i= 0; i < length; i++){
                        arr[i] = PackageManager.PERMISSION_GRANTED;
                    }
                    mResultFunc.call(reqCode, mPermissionNames, arr);
                    return this;
                }
                if (mGrantFunc != null) {
                    mGrantFunc.call();
                }
            }
            return this;
        }

        private boolean needToAsk() {
            ArrayList<SinglePermission> neededPermissions = new ArrayList<>(mPermissionsWeDontHave);
            for (int i = 0; i < mPermissionsWeDontHave.size(); i++) {
                SinglePermission perm = mPermissionsWeDontHave.get(i);
                int checkRes;
                if (mAppCompatActivity != null) {
                    checkRes = ContextCompat.checkSelfPermission(mAppCompatActivity, perm.getPermissionName());
                } else if (mActivity != null) {
                    checkRes = ContextCompat.checkSelfPermission(mActivity, perm.getPermissionName());
                } else {
                    checkRes = ContextCompat.checkSelfPermission(mFragment.getContext(), perm.getPermissionName());
                }
                if (checkRes == PackageManager.PERMISSION_GRANTED) {
                    neededPermissions.remove(perm);
                } else {
                    boolean shouldShowRequestPermissionRationale;
                    if (mAppCompatActivity != null) {
                        shouldShowRequestPermissionRationale =
                                ActivityCompat.shouldShowRequestPermissionRationale(mAppCompatActivity, perm.getPermissionName());
                    } else if (mActivity != null) {
                        shouldShowRequestPermissionRationale =
                                ActivityCompat.shouldShowRequestPermissionRationale(mActivity, perm.getPermissionName());
                    } else {
                        shouldShowRequestPermissionRationale = mFragment.shouldShowRequestPermissionRationale(perm.getPermissionName());
                    }
                    if (shouldShowRequestPermissionRationale) {
                        perm.setRationalNeeded(true);
                    }
                }
            }
            mPermissionsWeDontHave = neededPermissions;
            mPermissionNames = new String[mPermissionsWeDontHave.size()];
            for (int i = 0; i < mPermissionsWeDontHave.size(); i++) {
                mPermissionNames[i] = mPermissionsWeDontHave.get(i).getPermissionName();
            }
            return mPermissionsWeDontHave.size() != 0;
        }

        /**
         * Called for the first denied permission if there is need to show the rational
         */
        public PermissionRequestObject onRational(FuncRational rationalFunc) {
            mRationalFunc = rationalFunc;
            return this;
        }

        /**
         * Called if all the permissions were granted
         */
        public PermissionRequestObject onAllGranted(FuncCall grantFunc) {
            mGrantFunc = grantFunc;
            return this;
        }

        /**
         * Called if there is at least one denied permission
         */
        public PermissionRequestObject onAnyDenied(FuncCall denyFunc) {
            mDenyFunc = denyFunc;
            return this;
        }

        /**
         * Called with the original operands
         */
        public PermissionRequestObject onResult(FuncResult resultFunc) {
            mResultFunc = resultFunc;
            return this;
        }

        /**
         * This Method should be called
         * <pre>
         * {@code
         *
         * public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
         *      if (mStoragePermissionRequest != null)
         *          mStoragePermissionRequest.onRequestPermissionsResult(requestCode, permissions,grantResults);
         * }
         * }
         * </pre>
         */
        public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
            if (mRequestCode == requestCode) {
                if (mResultFunc != null) {
//                    Log.i(TAG, "Calling Results FuncCall");
                    mResultFunc.call(requestCode, permissions, grantResults);
                    return;
                }

                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        if (mPermissionsWeDontHave.get(i).isRationalNeeded() && mRationalFunc != null) {
//                            Log.i(TAG, "Calling Rational FuncCall");
                            mRationalFunc.call(mPermissionsWeDontHave.get(i).getPermissionName());
                        } else if (mDenyFunc != null) {
//                            Log.i(TAG, "Calling Deny FuncCall");
                            mDenyFunc.call();
                        } else {
                            Log.e(TAG, "NUll DENY FUNCTIONS");
                        }

                        // terminate if there is at least one deny
                        return;
                    }
                }

                // there has not been any deny
                if (mGrantFunc != null) {
//                    Log.i(TAG, "Calling Grant FuncCall");
                    mGrantFunc.call();
                } else {
                    Log.e(TAG, "NUll GRANT FUNCTIONS");
                }
            }
        }
    }
}
