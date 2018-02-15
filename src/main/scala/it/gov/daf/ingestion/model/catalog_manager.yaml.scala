package catalog_manager


    import scala.math.BigInt


//noinspection ScalaStyle
package yaml {

    case class DatasetCatalog(avro: Avro, flatSchema: DatasetCatalogFlatSchema, kyloSchema: MetadataCat)
    case class ConversionField(field_std: String, formula: String)
    case class Extra(key: MetadataCat, value: MetadataCat)
    case class Semantic(id: String, context: MetadataCat)
    case class StdSchema(std_uri: String, fields_conv: StdSchemaFields_conv)
    case class StorageHdfs(name: String, path: MetadataCat, param: MetadataCat)
    case class Metadata(semantics: MetadataSemantics, desc: MetadataCat, tag: OperationalIngestion_pipeline, field_type: MetadataCat, constr: MetadataConstr, required: MetadataRequired, cat: MetadataCat)
    case class Constr(`type`: MetadataCat, param: MetadataCat)
    case class User(name: MetadataCat, email: MetadataCat, fullname: MetadataCat, about: MetadataCat, password: MetadataCat)
    case class VocKeyValueSubtheme(key: String, value: String, keyTheme: String, valueTheme: MetadataCat)
    case class Tag(name: MetadataCat, state: MetadataCat, vocabulary_id: MetadataCat, display_name: MetadataCat, id: MetadataCat)
    case class StdUris(label: MetadataCat, value: MetadataCat)
    case class Success(message: String, fields: MetadataCat)
    case class Avro(name: String, `type`: String, namespace: String, aliases: OperationalIngestion_pipeline, fields: AvroFields)
    case class Group(name: MetadataCat, description: MetadataCat, display_name: MetadataCat, image_display_url: MetadataCat, id: MetadataCat, title: MetadataCat)
    case class Credentials(username: MetadataCat, password: MetadataCat)
    case class UserOrg(name: MetadataCat, capacity: MetadataCat)
    case class GeoRef(lat: Double, lon: Double)
    case class Operational(dataset_type: String, read_type: String, is_std: Boolean, logical_uri: String, theme: String, subtheme: String, group_own: String, georef: OperationalGeoref, storage_info: OperationalStorage_info, physical_uri: MetadataCat, ingestion_pipeline: OperationalIngestion_pipeline, input_src: InputSrc, group_access: OperationalGroup_access, std_schema: OperationalStd_schema)
    case class SourceDafDataset(dataset_uri: OperationalIngestion_pipeline, sql: MetadataCat, param: MetadataCat)
    case class Field(name: String, `type`: String)
    case class SourceSftp(name: String, url: MetadataCat, username: MetadataCat, param: MetadataCat, password: MetadataCat)
    case class Relationship(subject: MetadataCat, `object`: MetadataCat, `type`: MetadataCat, comment: MetadataCat)
    case class StorageInfo(hbase: StorageInfoHbase, mongo: StorageInfoMongo, kudu: StorageInfoKudu, textdb: StorageInfoMongo, hdfs: StorageInfoMongo)
    case class Token(token: MetadataCat)
    case class AutocompRes(match_field: MetadataCat, match_displayed: MetadataCat, name: MetadataCat, title: MetadataCat)
    case class Dataset(name: String, notes: String, organization: DatasetOrganization, author: MetadataCat, license_id: MetadataCat, relationships_as_object: DatasetRelationships_as_subject, holder_identifier: MetadataCat, identifier: MetadataCat, tags: DatasetTags, groups: DatasetGroups, modified: MetadataCat, privatex: OrganizationIs_organization, alternate_identifier: MetadataCat, relationships_as_subject: DatasetRelationships_as_subject, holder_name: MetadataCat, publisher_identifier: MetadataCat, resources: DatasetResources, frequency: MetadataCat, title: MetadataCat, owner_org: MetadataCat, theme: MetadataCat, publisher_name: MetadataCat)
    case class MetaCatalog(dataschema: DatasetCatalog, operational: Operational, dcatapit: Dataset)
    case class InputSrc(sftp: InputSrcSftp, srv_pull: InputSrcSrv_push, srv_push: InputSrcSrv_push, daf_dataset: InputSrcDaf_dataset)
    case class KeyValue(key: String, value: String)
    case class Lang(eng: MetadataCat, ita: MetadataCat)
    case class ConversionSchema(fields_conv: StdSchemaFields_conv, fields_custom: ConversionSchemaFields_custom)
    case class SourceSrvPush(name: String, url: String, access_token: MetadataCat, username: MetadataCat, param: MetadataCat, password: MetadataCat)
    case class StorageKudu(name: String, table_name: MetadataCat, param: MetadataCat)
    case class Error(message: String, code: MetadataRequired, fields: MetadataCat)
    case class Resource(mimetype: MetadataCat, format: MetadataCat, name: MetadataCat, package_id: MetadataCat, datastore_active: OrganizationIs_organization, size: ResourceSize, state: MetadataCat, url: MetadataCat, description: MetadataCat, resource_type: MetadataCat, distribution_format: MetadataCat, last_modified: MetadataCat, hash: MetadataCat, id: MetadataCat, cache_url: MetadataCat, position: ResourceSize, mimetype_inner: MetadataCat, cache_last_updated: MetadataCat, revision_id: MetadataCat, created: MetadataCat)
    case class StorageHbase(name: String, metric: MetadataCat, tags: OperationalIngestion_pipeline, param: MetadataCat)
    case class GroupAccess(name: String, role: String)
    case class Organization(name: String, image_url: MetadataCat, email: MetadataCat, state: MetadataCat, description: MetadataCat, users: OrganizationUsers, is_organization: OrganizationIs_organization, id: MetadataCat, title: MetadataCat, `type`: MetadataCat, revision_id: MetadataCat, approval_status: MetadataCat, created: MetadataCat)
    case class FlatSchema(name: String, `type`: String, metadata: FlatSchemaMetadata)
    case class CustomField(name: String)

}

// should be defined after the package because of the https://issues.scala-lang.org/browse/SI-9922

//noinspection ScalaStyle
package object yaml {

    type StorageInfoMongo = Option[StorageHdfs]
    type MetadataCat = Option[String]
    type MetadataConstr = Option[MetadataConstrOpt]
    type MetadataTagOpt = Seq[String]
    type DatasetCatalogFlatSchema = Seq[FlatSchema]
    type Dataset_catalogsGetResponses200 = Seq[MetaCatalog]
    type Dataset_catalogsGetLimit = Option[Int]
    type MetadataRequired = Option[Int]
    type MetadataSemantics = Option[Semantic]
    type DatasetResourcesOpt = Seq[Resource]
    type InputSrcSrv_push = Option[InputSrcSrv_pullOpt]
    type CkanSearchDatasetGetResponses200 = Seq[Dataset]
    type InputSrcSftpOpt = Seq[SourceSftp]
    type InputSrcSftp = Option[InputSrcSftpOpt]
    type VocDcatthemesDaf2dcatThemeidGetResponses200 = Seq[KeyValue]
    type OrganizationUsers = Option[OrganizationUsersOpt]
    type OrganizationIs_organization = Option[Boolean]
    type DatasetOrganization = Option[Organization]
    type OperationalIngestion_pipeline = Option[MetadataTagOpt]
    type OperationalGroup_accessOpt = Seq[GroupAccess]
    type ResourceSize = Option[BigInt]
    type OperationalGroup_access = Option[OperationalGroup_accessOpt]
    type VocSubthemesGetallGetResponses200 = Seq[VocKeyValueSubtheme]
    type DatasetTags = Option[DatasetTagsOpt]
    type AvroFieldsOpt = Seq[Field]
    type CkanUserOrganizationsUsernameGetResponses200 = Seq[Organization]
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
    type DatasetGroupsOpt = Seq[Group]
    type OperationalStorage_info = Option[StorageInfo]
    type OperationalStd_schema = Option[StdSchema]

}
