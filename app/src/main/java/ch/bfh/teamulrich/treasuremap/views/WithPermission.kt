package ch.bfh.teamulrich.treasuremap.views

import android.Manifest
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun WithPermission(permission: String, noPermissionContent: @Composable (PermissionState) -> Unit, children: @Composable () -> Unit = {}) {
    val fineLocationPermission = rememberPermissionState(permission)
    if (!fineLocationPermission.status.isGranted) {
        noPermissionContent(fineLocationPermission)
    } else {
        children()
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Preview
@Composable
fun WithPermissionPreview() {
    WithPermission(
        permission = Manifest.permission.ACCESS_FINE_LOCATION,
        noPermissionContent = {
            Button(onClick = { it.launchPermissionRequest() }) {
                Text("No :(")
            }
        }) {
        Text("Yes")
    }
}
