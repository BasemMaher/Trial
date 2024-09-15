package com_2is.egypt.wipegadmin.ui.sub_features.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com_2is.egypt.wipegadmin.R
import com_2is.egypt.wipegadmin.entites.Item
import com_2is.egypt.wipegadmin.entites.ServerMaterial
import com_2is.egypt.wipegadmin.entites.UploadMaterial

@Composable
fun ItemCard(item: Item, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .padding(16.dp), shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            item.run {
                PropertyText(label = stringResource(R.string.code), value = code)
                PropertyText(label = stringResource(R.string.class_code), value = classCode)
                Row {
                    PropertyText(
                        label = stringResource(R.string.size),
                        value = size,
                        modifier = Modifier.weight(1f)
                    )
                    PropertyText(
                        label = stringResource(R.string.volt),
                        value = volt,
                        modifier = Modifier.weight(1f)
                    )

                }

                PropertyText(label = stringResource(R.string.description), value = desc)
            }
        }
    }

}


@Composable
fun UploadMaterialCard(material: UploadMaterial, showUploadState: Boolean = false) {
    Card {
        Row {
            Surface(
                elevation = 8.dp,
                modifier = Modifier
                    .wrapContentWidth(),
                shape = MaterialTheme.shapes.medium,
                color = Color.LightGray
            ) {

                Text(
                    text = material.serial.toString(),
                    modifier = Modifier
                        .padding(8.dp),
                    color =
                    Color.Black
                )
            }

            Column(
                Modifier
                    .padding(8.dp)
                    .weight(1f)
            ) {
                material.apply {
                    PropertyText(label = "Work Order", value = Wo)
                    PropertyText(label = "Qty", value = qty)
                    PropertyText(label = "Note", value = note)
                    PropertyText(label = "Rm Code", value = rmCode)
                    PropertyText(label = "Uom Code", value = uomCode)
                }

            }
            if (showUploadState)
                UploadStateIcon(state = material.uploadState)
        }

    }

}

@Composable
fun ServerMaterialCard(material: ServerMaterial) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            PropertyText(label = "Description ", value = material.description)
            Row {
                PropertyText(label = "RmCode ", value = material.rmCode,
                    modifier = Modifier.weight(1f))
                PropertyText(
                    label = "UOMCode",
                    value = material.uomCode,
                    modifier = Modifier.weight(1f)
                )

            }

        }

    }

}
