/*
 * Copyright 2017 - 2018 TEAM PER LA TRASFORMAZIONE DIGITALE
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.gov.daf.catalog_manager

import scala.math.BigInt

package yaml {

  case class ConversionField(field_std: String, formula: String)
  case class Extra(key: MetadataCat, value: MetadataCat)
  case class Partitions(name: String, field: String, formula: String)
  case class StdSchema(std_uri: String, fields_conv: StdSchemaFields_conv)
  case class ExtOpenData(resourceId: String, name: String, url: String, resourceName: String, id: String, resourceUrl: String)
  case class Personal(ispersonal: Boolean, cat: MetadataCat)
  case class StorageHdfs(name: String, path: MetadataCat, param: MetadataCat)
  case class Metadata(field_profile: MetadataField_profile, semantics: MetadataSemantics, desc: MetadataCat, format_std: MetadataFormat_std, tag: FieldProfileStandardization, uniq_dim: MetadataIs_createdate, field_type: MetadataCat, is_createdate: MetadataIs_createdate, is_updatedate: MetadataIs_createdate, personal: MetadataPersonal, constr: MetadataConstr, title: MetadataCat, required: MetadataRequired, cat: MetadataCat)
  case class Constr(`type`: MetadataCat, param: MetadataCat)
  case class User(name: MetadataCat, email: MetadataCat, fullname: MetadataCat, about: MetadataCat, password: MetadataCat)
  case class VocKeyValueSubtheme(key: String, value: String, keyTheme: String, valueTheme: MetadataCat)
  case class Tag(name: MetadataCat, state: MetadataCat, vocabulary_id: MetadataCat, display_name: MetadataCat, id: MetadataCat)
  case class StdUris(label: MetadataCat, value: MetadataCat)
  case class Success(message: String, fields: MetadataCat)
  case class Avro(name: String, `type`: String, namespace: String, aliases: FieldProfileStandardization, fields: AvroFields)
  case class Group(name: MetadataCat, description: MetadataCat, display_name: MetadataCat, image_display_url: MetadataCat, id: MetadataCat, title: MetadataCat)
  case class Credentials(username: MetadataCat, password: MetadataCat)
  case class UserOrg(name: MetadataCat, capacity: MetadataCat)
  case class GeoRef(lat: Double, lon: Double)
  case class Operational(dataset_type: String, read_type: String, is_std: Boolean, logical_uri: String, theme: String, subtheme: String, group_own: String, georef: OperationalGeoref, storage_info: OperationalStorage_info, ext_opendata: OperationalExt_opendata, physical_uri: MetadataCat, ingestion_pipeline: FieldProfileStandardization, input_src: InputSrc, group_access: OperationalGroup_access, std_schema: OperationalStd_schema, partitions: DatasetProcPartitions, dataset_proc: OperationalDataset_proc)
  case class SourceDafDataset(dataset_uri: FieldProfileStandardization, sql: MetadataCat, param: MetadataCat)
  case class Field(name: String, `type`: String)
  case class SourceSftp(name: String, url: MetadataCat, username: MetadataCat, param: MetadataCat, password: MetadataCat)
  case class Relationship(subject: MetadataCat, `object`: MetadataCat, `type`: MetadataCat, comment: MetadataCat)
  case class StorageInfo(hbase: StorageInfoHbase, mongo: StorageInfoMongo, kudu: StorageInfoKudu, textdb: StorageInfoMongo, hdfs: StorageInfoMongo)
  case class Token(token: MetadataCat)
  case class DatasetProc(dataset_type: String, cron: String, read_type: String, merge_strategy: String, partitions: DatasetProcPartitions)
  case class AutocompRes(match_field: MetadataCat, match_displayed: MetadataCat, name: MetadataCat, title: MetadataCat)
  case class Dataset(name: String, notes: String, organization: DatasetOrganization, author: MetadataCat, license_id: MetadataCat, relationships_as_object: DatasetRelationships_as_subject, holder_identifier: MetadataCat, identifier: MetadataCat, tags: DatasetTags, groups: DatasetGroups, modified: MetadataCat, privatex: MetadataIs_createdate, alternate_identifier: MetadataCat, relationships_as_subject: DatasetRelationships_as_subject, holder_name: MetadataCat, publisher_identifier: MetadataCat, resources: DatasetResources, frequency: MetadataCat, title: MetadataCat, owner_org: MetadataCat, theme: MetadataCat, publisher_name: MetadataCat)
  case class MetaCatalog(dataschema: DatasetCatalog, operational: Operational, dcatapit: Dataset)
  case class InputSrc(sftp: InputSrcSftp, srv_pull: InputSrcSrv_push, srv_push: InputSrcSrv_push, daf_dataset: InputSrcDaf_dataset)
  case class KeyValue(key: String, value: String)
  case class Lang(eng: MetadataCat, ita: MetadataCat)
  case class ConversionSchema(fields_conv: StdSchemaFields_conv, fields_custom: ConversionSchemaFields_custom)
  case class SourceSrvPush(name: String, url: String, access_token: MetadataCat, username: MetadataCat, param: MetadataCat, password: MetadataCat)
  case class StorageKudu(name: String, table_name: MetadataCat, param: MetadataCat)
  case class Error(message: String, code: MetadataRequired, fields: MetadataCat)
  case class Resource(mimetype: MetadataCat, format: MetadataCat, name: MetadataCat, package_id: MetadataCat, datastore_active: MetadataIs_createdate, size: ResourceSize, state: MetadataCat, url: MetadataCat, description: MetadataCat, resource_type: MetadataCat, distribution_format: MetadataCat, last_modified: MetadataCat, hash: MetadataCat, id: MetadataCat, cache_url: MetadataCat, position: ResourceSize, mimetype_inner: MetadataCat, cache_last_updated: MetadataCat, revision_id: MetadataCat, created: MetadataCat)
  case class StorageHbase(name: String, metric: MetadataCat, tags: FieldProfileStandardization, param: MetadataCat)
  case class FieldProfile(is_index: MetadataIs_createdate, is_profile: MetadataIs_createdate, validation: FieldProfileStandardization, standardization: FieldProfileStandardization)
  case class GroupAccess(name: String, role: String)
  case class Organization(name: String, image_url: MetadataCat, email: MetadataCat, state: MetadataCat, description: MetadataCat, users: OrganizationUsers, is_organization: MetadataIs_createdate, id: MetadataCat, title: MetadataCat, `type`: MetadataCat, revision_id: MetadataCat, approval_status: MetadataCat, created: MetadataCat)
  case class FlatSchema(name: String, `type`: String, metadata: FlatSchemaMetadata)
  case class CustomField(name: String)

 // Ingestion changes
  // case class FormatStd(name: String, param: MetadataCat)
  case class FormatStd(name: String, param: FormatParams, conv: FormatConv)
  case class KeyValues(key: String, value: Seq[String])

  // case class Semantic(id: String, predicate: MetadataCat, subject: MetadataCat, context: MetadataCat, id_label: MetadataCat, context_label: MetadataCat, uri_voc: MetadataCat, property_hierarchy: FieldProfileStandardization, rdf_object: MetadataCat, uri_property: MetadataCat)
  case class Semantic(id: String, predicate: MetadataCat, subject: MetadataCat, context: MetadataCat, id_label: MetadataCat, context_label: MetadataCat, uri_voc: MetadataCat, property_hierarchy: FieldProfileStandardization, rdf_object: MetadataCat, uri_property: MetadataCat, field_group: MetadataCat)

  // case class DatasetCatalog(avro: Avro, flatSchema: DatasetCatalogFlatSchema, kyloSchema: MetadataCat)
  case class DatasetCatalog(avro: Avro, flatSchema: DatasetCatalogFlatSchema, kyloSchema: MetadataCat, encoding: MetadataCat)

}

package object yaml {

 // Ingestion changes
  type FormatConv = Option[KeyValuesArray]
  type KeyValuesArray = Seq[KeyValues]
  type FormatParams = Option[KeyValueArray]
  type KeyValueArray = Seq[KeyValue]

  type StorageInfoMongo = Option[StorageHdfs]
  type MetadataCat = Option[String]
  type FieldProfileStandardization = Option[MetadataTagOpt]
  type MetadataIs_createdate = Option[Boolean]
  type MetadataConstr = Option[MetadataConstrOpt]
  type MetadataTagOpt = Seq[String]
  type MetadataFormat_std = Option[FormatStd]
  type DatasetCatalogFlatSchema = Seq[FlatSchema]
  type Dataset_catalogsGetResponses200 = Seq[MetaCatalog]
  type Dataset_catalogsGetLimit = Option[Int]
  type MetadataRequired = Option[Int]
  type MetadataSemantics = Option[Semantic]
  type DatasetResourcesOpt = Seq[Resource]
  type InputSrcSrv_push = Option[InputSrcSrv_pullOpt]
  type CkanSearchDatasetGetResponses200 = Seq[Dataset]
  type InputSrcSftpOpt = Seq[SourceSftp]
  type MetadataPersonal = Option[Personal]
  type OperationalDataset_proc = Option[DatasetProc]
  type InputSrcSftp = Option[InputSrcSftpOpt]
  type VocDcatthemesDaf2dcatThemeidGetResponses200 = Seq[KeyValue]
  type OrganizationUsers = Option[OrganizationUsersOpt]
  type DatasetOrganization = Option[Organization]
  type DatasetProcPartitions = Option[Partitions]
  type OperationalGroup_accessOpt = Seq[GroupAccess]
  type ResourceSize = Option[BigInt]
  type OperationalGroup_access = Option[OperationalGroup_accessOpt]
  type VocSubthemesGetallGetResponses200 = Seq[VocKeyValueSubtheme]
  type DatasetTags = Option[DatasetTagsOpt]
  type AvroFieldsOpt = Seq[Field]
  type CkanUserOrganizationsUsernameGetResponses200 = Seq[Organization]
  type MetadataField_profile = Option[FieldProfile]
  type AvroFields = Option[AvroFieldsOpt]
  type FlatSchemaMetadata = Option[Metadata]
  type DatasetResources = Option[DatasetResourcesOpt]
  type InputSrcDaf_dataset = Option[InputSrcDaf_datasetOpt]
  type ConversionSchemaFields_custom = Option[ConversionSchemaFields_customOpt]
  type InputSrcDaf_datasetOpt = Seq[SourceDafDataset]
  type StorageInfoHbase = Option[StorageHbase]
  type DatasetGroups = Option[DatasetGroupsOpt]
  type DatasetRelationships_as_subject = Option[DatasetRelationships_as_subjectOpt]
  type OrganizationUsersOpt = Seq[UserOrg]
  type StdSchemaFields_conv = Seq[ConversionField]
  type OperationalGeoref = Option[OperationalGeorefOpt]
  type ConversionSchemaFields_customOpt = Seq[CustomField]
  type MetadataConstrOpt = Seq[Constr]
  type InputSrcSrv_pullOpt = Seq[SourceSrvPush]
  type DatasetTagsOpt = Seq[Tag]
  type Dataset_catalogsStandard_urisGetResponses200 = Seq[StdUris]
  type StorageInfoKudu = Option[StorageKudu]
  type DatasetRelationships_as_subjectOpt = Seq[Relationship]
  type CkanAutocompleteDatasetGetResponses200 = Seq[AutocompRes]
  type OperationalGeorefOpt = Seq[GeoRef]
  type OperationalExt_opendata = Option[ExtOpenData]
  type DatasetGroupsOpt = Seq[Group]
  type OperationalStorage_info = Option[StorageInfo]
  type OperationalStd_schema = Option[StdSchema]

}
